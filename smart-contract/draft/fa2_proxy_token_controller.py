import smartpy as sp

################## FA2 Entrypoint parameters types ##################

contract_metadata_type = sp.TRecord(
    k = sp.TString,
    v= sp.TBytes
    )

operator_param_type = sp.TRecord(
    owner = sp.TAddress,
    operator = sp.TAddress,
    token_id = sp.TNat
    ).layout(("owner", ("operator", "token_id")))

update_operators_params_type = sp.TList(
    sp.TVariant(
        add_operator = operator_param_type,
        remove_operator = operator_param_type
        )
    )

transfer_type = sp.TRecord(
    from_ = sp.TAddress,
    txs = sp.TList(
        sp.TRecord(
            to_ = sp.TAddress,
            token_id = sp.TNat,
            amount = sp.TNat
            ).layout(("to_", ("token_id", "amount")))
        )
    ).layout(("from_", "txs"))

transfer_params_type = sp.TList(transfer_type)

batch_mint_params_type = sp.TRecord(
    metadata_links = sp.TList(sp.TBytes),
    address = sp.TAddress
    )

fa2_batch_mint_params_type = sp.TRecord(
    metadata_links = sp.TList(sp.TBytes),
    first_token_id = sp.TNat,
    address = sp.TAddress
    )

token_metadata_type = sp.TMap(sp.TString, sp.TBytes)

mint_params_type = sp.TRecord(
    amount = sp.TNat,
    address = sp.TAddress,
    metadata = token_metadata_type
    )

fa2_mint_params_type = sp.TRecord(
    token_id = sp.TNat,
    amount = sp.TNat,
    address = sp.TAddress,
    metadata = token_metadata_type
    )

fa2_params_types = {
    "mint": fa2_mint_params_type,
    "update_operators": update_operators_params_type,
    "transfer": transfer_params_type,
    "set_pause": sp.TBool,
    "set_metadata": contract_metadata_type,
    "set_administrator": sp.TAddress,
    "batch_mint": fa2_batch_mint_params_type
    }

################## Proxy types ##################

rule_type = sp.TRecord(
    template_id = sp.TNat,
    authorized_builders = sp.TSet(sp.TString) # group names
)

template_type = sp.TRecord(
    contract_threshold = sp.TNat,
    groups = sp.TMap(sp.TString, sp.TRecord(group_threshold = sp.TNat, group_weight = sp.TNat))
)

update_groups_type = sp.TList(
    sp.TVariant(
            add = sp.TMap(sp.TString, sp.TAddress),
            remove = sp.TMap(sp.TString, sp.TAddress)
    )
)
update_templates_type = sp.TList(
    sp.TVariant(
            update = sp.TMap(sp.TNat, template_type),
            remove = sp.TList(sp.TNat)
    )
)
update_rules_type = sp.TList(
    sp.TVariant(
            update = sp.TMap(sp.TString, rule_type),
            remove = sp.TList(sp.TString)
    )
)

multisig_build_params_type = sp.TRecord(
    multisig_id = sp.TNat,
    signers = sp.TList(sp.TAddress),
    build_and_sign = sp.TBool,
    call_params = sp.TRecord(
        target_address = sp.TOption(sp.TAddress),
        entry_point = sp.TString,
        parameters = sp.TVariant(
            mint = sp.TRecord(mint_params = mint_params_type, operator = sp.TOption(sp.TAddress)),
            update_operators = update_operators_params_type,
            transfer = transfer_params_type,
            set_pause = sp.TBool,
            set_metadata = contract_metadata_type,
            set_administrator = sp.TAddress,
            batch_mint = sp.TRecord(batch_mint_params = batch_mint_params_type, operator = sp.TOption(sp.TAddress)),
            update_groups = update_groups_type,
            update_templates = update_templates_type,
            update_rules = update_rules_type
            )
        )
    )


class ProxyTokenController(sp.Contract):
    def __init__(self, groups, multisig_templates, rules):
        self.init(
            multisigs = sp.big_map(),
            multisig_nb = 0,
            groups = sp.map(
                l = groups,
                tkey = sp.TString,
                tvalue = sp.TSet(sp.TAddress)
            ),
            multisig_templates = sp.map(
                l = multisig_templates,
                tkey = sp.TNat,
                tvalue = template_type
            ),
            rules = sp.map(
                l = rules,
                tkey = sp.TString, # entrypoint
                tvalue = rule_type
            )
        )

    # define a multisig contract
    @sp.entry_point
    def build(self, params):
        sp.set_type(params, multisig_build_params_type)
        sp.verify(self.data.multisig_nb == params.multisig_id, "INVALID_MULTISIG_ID")
        self.data.multisig_nb += 1
        sp.verify(self.data.rules.contains(params.call_params.entry_point), "NO_RULE_FOR_THIS_ENTRYPOINT")
        rule = self.data.rules[params.call_params.entry_point]
        self._is_builder(sp.sender, rule.authorized_builders)

        sp.verify(self.data.multisig_templates.contains(rule.template_id), "UNKNOWN_TEMPLATE")
        rule_template = self.data.multisig_templates[rule.template_id]

        participants = sp.local('participants', sp.list([]))
        groups = sp.local('groups', sp.list([]))
        sp.for group in rule_template.groups.items():
        # empty string group means participants are passed in parameters
            sp.if group.key == "":
                sp.for signer in params.signers:
                    participants.value.push(sp.record(hasVoted = False, id = signer))
            sp.else:
                sp.for signer in self.data.groups[group.key].elements():
                    participants.value.push(sp.record(hasVoted = False, id = signer))
            groups.value.push(sp.record(
                participants = participants.value,
                votes = 0,
                ok = False,
                group_threshold = group.value.group_threshold,
                group_weight = group.value.group_weight
            ))
            participants.value = sp.list([])

        contract = sp.record(
             weight          = 0,
             contract_threshold = rule_template.contract_threshold,
             groups          = groups.value,
             ok              = False,
             call_params      = params.call_params
            )
        self.data.multisigs[params.multisig_id] = contract
        sp.if params.build_and_sign:
            self._sign(sp.record(contractId = params.multisig_id, id = sp.sender))

    # sign a multisig contract
    @sp.entry_point
    def sign(self, contractId):
        self._sign(sp.record(id = sp.sender, contractId = contractId))

    def _sign(self, params):
        id = params.id
        contractId = params.contractId
        contract = self.data.multisigs[contractId]
        sp.for group in contract.groups:
            sp.for participant in group.participants:
                sp.if participant.id == id:
                    sp.verify(~participant.hasVoted, "PARTICIPANT_ALREADY_VOTED")
                    participant.hasVoted = True
                    group.votes += 1
                    sp.if ~group.ok & (group.group_threshold <= group.votes):
                        group.ok = True
                        contract.weight += group.group_weight
                        sp.if ~contract.ok & (contract.contract_threshold <= contract.weight):
                            contract.ok = True
                            self.onOK(contract.call_params)

    def onOK(self, params):
        entrypoint_error = "PARAMETERS_AND_ENTRYPOINT_ARE_NOT_COMPATIBLE"
        target_address_error = "TARGET_ADDRESS_CANNOT_BE_NONE"
        target_address = params.target_address.open_some(target_address_error)
        with params.parameters.match_cases() as arg:
            with arg.match("mint") as parameters:
                sp.verify(params.entry_point == "mint", entrypoint_error)
                token_id = sp.view("all_tokens", target_address, sp.unit, t = sp.TNat).open_some("Invalid all_tokens view")
                mint_params = sp.record(
                    token_id = token_id,
                    amount = parameters.mint_params.amount,
                    metadata = parameters.mint_params.metadata,
                    address = parameters.mint_params.address
                )
                self._execute_call("mint", mint_params, target_address)
                sp.if parameters.operator.is_some():
                    # add operator along with the mint
                    add_op_parameters = [
                        sp.variant("add_operator", sp.record(
                            owner = parameters.mint_params.address,
                            operator = parameters.operator.open_some("OPERATOR_PARAM_NOT_FOUND"),
                            token_id = token_id
                        ))
                    ]
                    self._execute_call("update_operators", add_op_parameters, target_address)
            with arg.match("transfer") as parameters:
                sp.verify(params.entry_point == "transfer", entrypoint_error)
                self._execute_call("transfer", parameters, target_address)
            with arg.match("update_operators") as parameters:
                sp.verify(params.entry_point == "update_operators", entrypoint_error)
                self._execute_call("update_operators", parameters, target_address)
            with arg.match("set_pause") as parameters:
                sp.verify(params.entry_point == "set_pause", entrypoint_error)
                self._execute_call("set_pause", parameters, target_address)
            with arg.match("set_metadata") as parameters:
                sp.verify(params.entry_point == "set_metadata", entrypoint_error)
                self._execute_call("set_metadata", parameters, target_address)
            with arg.match("set_administrator") as parameters:
                sp.verify(params.entry_point == "set_administrator", entrypoint_error)
                self._execute_call("set_administrator", parameters, target_address)
            with arg.match("batch_mint") as parameters:
                sp.verify(params.entry_point == "batch_mint", entrypoint_error)
                first_token_id = sp.view("all_tokens", target_address, sp.unit, t = sp.TNat).open_some("Invalid all_tokens view")
                batch_mint_params = sp.record(
                    first_token_id = first_token_id,
                    metadata_links = parameters.batch_mint_params.metadata_links,
                    address = parameters.batch_mint_params.address
                )
                self._execute_call("batch_mint", batch_mint_params, target_address)
                batch_length = sp.len(parameters.batch_mint_params.metadata_links)
                # add operator along with the batch_mint
                sp.if parameters.operator.is_some():
                    add_op_parameters = sp.local('add_op_parameters', sp.list([]))
                    sp.for token_id in sp.range(first_token_id, first_token_id + batch_length, step = 1):
                        add_op_parameters.value.push(sp.variant("add_operator", sp.record(
                            owner = parameters.batch_mint_params.address,
                            operator = parameters.operator.open_some("OPERATOR_PARAM_NOT_FOUND"),
                            token_id = token_id
                        )))
                    self._execute_call("update_operators", add_op_parameters.value, target_address)
            with arg.match("update_groups") as parameters:
                sp.verify(params.entry_point == "update_groups", entrypoint_error)
                self.update_groups(parameters)
            with arg.match("update_templates") as parameters:
                sp.verify(params.entry_point == "update_templates", entrypoint_error)
                self.update_templates(parameters)
            with arg.match("update_rules") as parameters:
                sp.verify(params.entry_point == "update_rules", entrypoint_error)
                self.update_rules(parameters)

    def _execute_call(self, entrypoint, parameters, target_address):
        sp.transfer(
            parameters,
            sp.tez(0),
            sp.contract(
                fa2_params_types[entrypoint],
                target_address,
                entry_point = entrypoint
                ).open_some()
            )

    def update_groups(self, params):
        sp.set_type(params, update_groups_type)
        sp.for updates in params:
            with updates.match_cases() as arg:
                with arg.match("add") as upd:
                    sp.for elt in upd.items():
                        sp.if ~ self.data.groups.contains(elt.key):
                            self.data.groups[elt.key] = sp.set([elt.value])
                        sp.else:
                            self.data.groups[elt.key].add(elt.value)
                with arg.match("remove") as upd:
                    sp.for elt in upd.items():
                        sp.if self.data.groups.contains(elt.key):
                            self.data.groups[elt.key].remove(elt.value)
                            sp.if sp.len(self.data.groups[elt.key]) == 0:
                                del self.data.groups[elt.key]

    def update_templates(self, params):
        sp.set_type(params, update_templates_type)
        sp.for updates in params:
            with updates.match_cases() as arg:
                with arg.match("update") as upd:
                    sp.for elt in upd.items():
                        self.data.multisig_templates[elt.key] = elt.value
                with arg.match("remove") as upd:
                    sp.for elt in upd:
                        del self.data.multisig_templates[elt]

    def update_rules(self, params: update_rules_type):
        sp.set_type(params, update_rules_type)
        sp.for updates in params:
            with updates.match_cases() as arg:
                with arg.match("update") as upd:
                    sp.for elt in upd.items():
                        self.data.rules[elt.key] = elt.value
                with arg.match("remove") as upd:
                    sp.for elt in upd:
                        del self.data.rules[elt]


    def _is_builder(self, sender: sp.TAddress, authorized_builders: sp.TSet(sp.TString)):
        # empty set means that any one can build
        sp.if sp.len(authorized_builders) != 0:
            authorized = sp.local('authorized', False)
            sp.for group in authorized_builders.elements():
                sp.verify(self.data.groups.contains(group), "UNKNOWN_GROUP")
                sp.for adr in self.data.groups[group].elements():
                    sp.if sender == adr:
                        authorized.value = True
            sp.verify(authorized.value, "NOT_AUTHORIZED_BUILDER")


# Tests
@sp.add_test(name = "ProxyTokenController")
def test():

    scenario = sp.test_scenario()

    super_fa2_admin1 = sp.address("tz1W5ubDUJwpd9Gb94V2YKnZBHggAMMxtbBd")
    super_fa2_admin2 = sp.address("tz1ZYKdGfkTHvcqyH5v9W9Sqjm25bdJ4A8yx")

    super_minter1 = sp.address("tz1hb8qHAQfYhDtpr8NH5GPqMbRBwd3C4Fef")
    super_minter2 = sp.address("tz1ZRraEmkupRpEhwaJwb58nysXTScXcvGYe")

    minter1 = sp.address("tz1hG1QqTMsd8XPpJkGwRmUeVBdNZSGDCPuQ")
    minter2 = sp.address("tz1eUi8B7G37jQAZgUMf39cECMKmn2e9zDxb")

    alice = sp.address("tz1TjFwiHUo13xxcrAW1a2pG8iXk2Goj5HRC")

    batch_minter = sp.address("tz1YWy3UMApD4D9YkjjHFrMn9ufatUf3kb89")

    proxy_admin = sp.address("tz1g7KBruSWP7JERBRhKgJzezPqs4ZBDkR5B")

    multisig_builder = sp.address("tz1dAbb66qRJndGMD4KKDxzBebQursit5pfT")

    target = sp.address("KT1aDD7H3WgbFgd7YeMn8UbqQeMXJ96kxvD5")

    groups = {
        "super_fa2_admins": sp.set([super_fa2_admin1, super_fa2_admin2]),
        "super_minters": sp.set([super_minter1, super_minter2]),
        "minters": sp.set([minter1, minter2]),
        "batch_minters": sp.set([batch_minter]),
        "multisig_builders": sp.set([multisig_builder]),
        "proxy_admins": sp.set([proxy_admin])
        }

    multisig_templates = {
        0: sp.record(
                contract_threshold = 1,
                groups = {
                    "super_fa2_admins": sp.record(group_threshold = 1, group_weight = 1)
                }
            ),
        1: sp.record(
                contract_threshold = 3,
                groups = {
                    "super_minters": sp.record(group_threshold = 1, group_weight = 1),
                    "minters": sp.record(group_threshold = 2, group_weight = 1),
                    "": sp.record(group_threshold = 1, group_weight = 2)
                }
            ),
        2: sp.record(
                contract_threshold = 1,
                groups = {
                    "batch_minters": sp.record(group_threshold = 1, group_weight = 1)
                }
            ),
        3: sp.record(
                contract_threshold = 1,
                groups = {
                    "proxy_admins": sp.record(group_threshold = 1, group_weight = 1)
                }
            )
    }

    rules = {
        "mint": sp.record(
            template_id = 1,
            authorized_builders = sp.set(["multisig_builders", "minters", "super_minters"])
        ),
        "batch_mint": sp.record(
            template_id = 2,
            authorized_builders = sp.set(["multisig_builders", "batch_minters"])
        ),
        "set_administrator": sp.record(
            template_id = 0,
            authorized_builders = sp.set(["super_fa2_admins"])
        ),
        "update_operators": sp.record(
            template_id = 0,
            authorized_builders = sp.set(["super_fa2_admins"])
        ),
        "transfer": sp.record(
            template_id = 0,
            authorized_builders = sp.set(["super_fa2_admins"])
        ),
        "set_pause": sp.record(
            template_id = 0,
            authorized_builders = sp.set(["super_fa2_admins"])
        ),
        "set_metadata": sp.record(
            template_id = 0,
            authorized_builders = sp.set([]) # in order to test if anyone can build when the list is empty
        ),
        "update_groups": sp.record(
            template_id = 3,
            authorized_builders = sp.set(["proxy_admins"])
        ),
        "update_templates": sp.record(
            template_id = 3,
            authorized_builders = sp.set(["proxy_admins"])
        ),
        "update_rules": sp.record(
            template_id = 3,
            authorized_builders = sp.set(["proxy_admins"])
        )
    }

    #PCC
    ## put the real initiator parameters, the contract will be initially deployed with the following parameters
    c0 = ProxyTokenController(
        groups,
        multisig_templates,
        rules
        )
    scenario += c0

    # DigitalPassport
    scenario += c0.build(
        build_and_sign = False,
        multisig_id = 0,
        signers = [alice], # Owner
        call_params = sp.record(
            target_address = sp.some(target),
            entry_point = "mint",
            parameters = sp.variant("mint", sp.record(
                mint_params = sp.record(
                    amount = 1,
                    metadata = {"": sp.bytes("0x050100000035697066733a2f2f516d5a36584762695a4d77664454325066436e71747462426e564d4a727473397670636867575251544b33337762")},
                    address = alice
                ),
                operator = sp.some(super_minter1)
            ))
        )
    ).run(sender = multisig_builder)
    scenario += c0.sign(0).run(sender = alice)
    scenario += c0.sign(0).run(sender = super_minter1)

    # DATA Catalog
    scenario += c0.build(
        multisig_id = 1,
        build_and_sign = True,
        signers = [],
        call_params = sp.record(
            target_address = sp.some(target),
            entry_point = "batch_mint",
            parameters = sp.variant("batch_mint", sp.record(
                batch_mint_params = sp.record(
                    metadata_links = [
                        sp.bytes("0x050100000035697066733a2f2f516d5a36584762695a4d77664454325066436e71747462426e564d4a727473397670636867575251544b33337762"),
                        sp.bytes("0x050200000035697066733a2f2f516d5a36584762695a4d77664454325066436e71747462426e564d4a727473397670636867575251544b33337762")
                    ],
                    address = alice
                ),
                operator = sp.some(minter2)
            ))
        )
    ).run(sender = batch_minter)

    # CREATE
    scenario += c0.build(
        multisig_id = 2,
        build_and_sign = False,
        signers = [],
        call_params = sp.record(
            target_address = sp.some(target),
            entry_point = "batch_mint",
            parameters = sp.variant("batch_mint", sp.record(
                batch_mint_params = sp.record(
                    metadata_links = [
                        sp.bytes("0x050100000035697066733a2f2f516d5a36584762695a4d77664454325066436e71747462426e564d4a727473397670636867575251544b33337762"),
                        sp.bytes("0x050200000035697066733a2f2f516d5a36584762695a4d77664454325066436e71747462426e564d4a727473397670636867575251544b33337762")
                    ],
                    address = alice
                ),
                operator = sp.some(minter2)
            ))
        )
    ).run(sender = batch_minter)

    # Non authorized builder
    scenario += c0.build(
        multisig_id = 3,
        build_and_sign = True,
        signers = [],
        call_params = sp.record(
            target_address = sp.some(target),
            entry_point = "transfer",
            parameters = sp.variant("transfer", [sp.record(
                from_ = alice,
                txs = [sp.record(
                    to_ = minter2,
                    token_id = 0,
                    amount = 1
                    )]
                )]),
        )
    ).run(sender = alice, valid = False)

    scenario += c0.build(
        multisig_id = 3,
        build_and_sign = True,
        signers = [],
        call_params = sp.record(
            target_address = sp.some(target),
            entry_point = "transfer",
            parameters = sp.variant("transfer", [sp.record(
                from_ = alice,
                txs = [sp.record(
                    to_ = minter2,
                    token_id = 0,
                    amount = 1
                    )]
                )]),
        )
    ).run(sender = super_fa2_admin1)

    # anyone can build when the list is empty

    scenario += c0.build(
        multisig_id = 4,
        build_and_sign = False,
        signers = [],
        call_params = sp.record(
            target_address = sp.some(target),
            entry_point = "set_metadata",
            parameters = sp.variant("set_metadata" , sp.record(
                k = "",
                v = sp.bytes("0x050100000035697066733a2f2f516d5a36584762695a4d77664454325066436e71747462426e564d4a727473397670636867575251544b33337762")
                )),
        )
    )

   # Test groups update
    scenario += c0.build(
        multisig_id = 5,
        build_and_sign = True,
        signers = [],
        call_params = sp.record(
            target_address = sp.none,
            entry_point = "update_groups",
            parameters = sp.variant("update_groups" , [sp.variant("add", {"minters": alice, "test": alice})])
        )
    ).run(sender = proxy_admin)
    #scenario.verify(c0.data.groups["minters"].contains(alice))
    #scenario.verify(c0.data.groups["test"].contains(alice))

    scenario += c0.build(
        multisig_id = 6,
        build_and_sign = True,
        signers = [],
        call_params = sp.record(
            target_address = sp.none,
            entry_point = "update_groups",
            parameters = sp.variant("update_groups" , [sp.variant("remove", {"minters": alice, "test": alice})])
        )
    ).run(sender = proxy_admin)
    #scenario.verify(~ c0.data.groups["minters"].contains(alice))
    #scenario.verify(~ c0.data.groups.contains("test"))

    # Test templates update

    scenario += c0.build(
        multisig_id = 7,
        build_and_sign = True,
        signers = [],
        call_params = sp.record(
            target_address = sp.none,
            entry_point = "update_templates",
            parameters = sp.variant("update_templates" , [sp.variant("update", {999: sp.record(
                    contract_threshold = 1,
                    groups = {"": sp.record(group_threshold = 1, group_weight = 1)}
                )})]
            )
        )
    ).run(sender = proxy_admin)
    #scenario.verify(c0.data.multisig_templates.contains(999))
    #scenario.verify(c0.data.multisig_templates[999].contract_threshold == 1)
    scenario += c0.build(
        multisig_id = 8,
        build_and_sign = True,
        signers = [],
        call_params = sp.record(
            target_address = sp.none,
            entry_point = "update_templates",
            parameters = sp.variant("update_templates" , [sp.variant("update", {999: sp.record(
                    contract_threshold = 2,
                    groups = {"": sp.record(group_threshold = 1, group_weight = 1)}
                )})]
            )
        )
    ).run(sender = proxy_admin)
    #scenario.verify(c0.data.multisig_templates[999].contract_threshold == 2)
    #scenario += c0.update_templates([sp.variant("remove", [999])]).run(sender = proxy_admin)
    #scenario.verify(~ c0.data.multisig_templates.contains(999))

    # Test rules update
    scenario += c0.build(
        multisig_id = 9,
        build_and_sign = True,
        signers = [],
        call_params = sp.record(
            target_address = sp.none,
            entry_point = "update_rules",
            parameters = sp.variant("update_rules" , [sp.variant("update", {"test": sp.record(
                    template_id = 0,
                    authorized_builders = sp.set(["super_fa2_admins"])
                )})]
            )
        )
    ).run(sender = proxy_admin)
    #scenario.verify(c0.data.rules.contains("test"))
    #scenario.verify(c0.data.rules["test"].template_id == 0)
    scenario += c0.build(
        multisig_id = 10,
        build_and_sign = True,
        signers = [],
        call_params = sp.record(
            target_address = sp.none,
            entry_point = "update_rules",
            parameters = sp.variant("update_rules" , [sp.variant("update", {"test": sp.record(
                    template_id = 1,
                    authorized_builders = sp.set(["super_fa2_admins"])
                )})]
            )
        )
    ).run(sender = proxy_admin)
    #scenario.verify(c0.data.rules["test"].template_id == 1)
    #scenario += c0.update_rules([sp.variant("remove", ["test"])]).run(sender = proxy_admin)
    #scenario.verify(~ c0.data.rules.contains("test"))
