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
        with sp.for_('group', rule_template.groups.items()) as group:
        # empty string group means participants are passed in parameters
            with sp.if_(group.key == ""):
                with sp.for_('signer', params.signers) as signer:
                    participants.value.push(sp.record(hasVoted = False, id = signer))
            with sp.else_():
                with sp.for_('signer', self.data.groups[group.key].elements()) as signer:
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
        with sp.if_(params.build_and_sign):
            self._sign(sp.record(contractId = params.multisig_id, id = sp.sender))

    # sign a multisig contract
    @sp.entry_point
    def sign(self, contractId):
        self._sign(sp.record(id = sp.sender, contractId = contractId))

    def _sign(self, params):
        id = params.id
        contractId = params.contractId
        contract = self.data.multisigs[contractId]
        with sp.for_('group', contract.groups) as group:
            with sp.for_('participant', group.participants) as participant:
                with sp.if_(participant.id == id):
                    sp.verify(~participant.hasVoted, "PARTICIPANT_ALREADY_VOTED")
                    participant.hasVoted = True
                    group.votes += 1
                    with sp.if_(~group.ok & (group.group_threshold <= group.votes)):
                        group.ok = True
                        contract.weight += group.group_weight
                        with sp.if_(~contract.ok & (contract.contract_threshold <= contract.weight)):
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
                with sp.if_(parameters.operator.is_some()):
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
                with sp.if_(parameters.operator.is_some()):
                    add_op_parameters = sp.local('add_op_parameters', sp.list([]))
                    with sp.for_('token_id', sp.range(first_token_id, first_token_id + batch_length, step = 1)) as token_id:
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
        with sp.for_('updates', params) as updates:
            with updates.match_cases() as arg:
                with arg.match("add") as upd:
                    with sp.for_('elt', upd.items()) as elt:
                        with sp.if_(~ self.data.groups.contains(elt.key)):
                            self.data.groups[elt.key] = sp.set([elt.value])
                        with sp.else_():
                            self.data.groups[elt.key].add(elt.value)
                with arg.match("remove") as upd:
                    with sp.for_('elt', upd.items()) as elt:
                        with sp.if_(self.data.groups.contains(elt.key)):
                            self.data.groups[elt.key].remove(elt.value)
                            with sp.if_(sp.len(self.data.groups[elt.key]) == 0):
                                del self.data.groups[elt.key]

    def update_templates(self, params):
        sp.set_type(params, update_templates_type)
        with sp.for_('updates', params) as updates:
            with updates.match_cases() as arg:
                with arg.match("update") as upd:
                    with sp.for_('elt', upd.items()) as elt:
                        self.data.multisig_templates[elt.key] = elt.value
                with arg.match("remove") as upd:
                    with sp.for_('elt', upd) as elt:
                        del self.data.multisig_templates[elt]

    def update_rules(self, params: update_rules_type):
        sp.set_type(params, update_rules_type)
        with sp.for_('updates', params) as updates:
            with updates.match_cases() as arg:
                with arg.match("update") as upd:
                    with sp.for_('elt', upd.items()) as elt:
                        self.data.rules[elt.key] = elt.value
                with arg.match("remove") as upd:
                    with sp.for_('elt', upd) as elt:
                        del self.data.rules[elt]


    def _is_builder(self, sender: sp.TAddress, authorized_builders: sp.TSet(sp.TString)):
        # empty set means that any one can build
        with sp.if_(sp.len(authorized_builders) != 0):
            authorized = sp.local('authorized', False)
            with sp.for_('group', authorized_builders.elements()) as group:
                sp.verify(self.data.groups.contains(group), "UNKNOWN_GROUP")
                with sp.for_('adr', self.data.groups[group].elements()) as adr:
                    with sp.if_(sender == adr):
                        authorized.value = True
            sp.verify(authorized.value, "NOT_AUTHORIZED_BUILDER")

# Tests
@sp.add_test(name = "ProxyTokenController")
def test():

    scenario = sp.test_scenario()

    minters = sp.set([
        sp.address('tz1WpmFuSZfuNS7XDKwDZxX3QhSNUneTkiTv')
    ])

    consortium = sp.set([
        sp.address('tz1WpmFuSZfuNS7XDKwDZxX3QhSNUneTkiTv'),
        sp.address('tz1XDwkYzcxqswBhQDd1QxiBrA53J5bZzHg2')
    ])

    scenario.show({"minters" : minters})
    scenario.show({"consortium" : consortium})

    groups = {
        "consortium": consortium,
        "minters": minters
        }

    multisig_templates = {
        0: sp.record(
                contract_threshold = 1,
                groups = {
                    "consortium": sp.record(group_threshold = sp.len(consortium), group_weight = 1)
                }
            ),
        1: sp.record(
                contract_threshold = 2,
                groups = {
                    "minters": sp.record(group_threshold = 1, group_weight = 1),
                    "": sp.record(group_threshold = 1, group_weight = 1)
                }
            )
    }

    rules = {
        "mint": sp.record(
            template_id = 1,
            authorized_builders = sp.set(["minters"])
        ),
        "batch_mint": sp.record(
            template_id = 1,
            authorized_builders = sp.set(["minters"])
        ),
        "set_administrator": sp.record(
            template_id = 0,
            authorized_builders = sp.set(["consortium"])
        ),
        "update_operators": sp.record(
            template_id = 0,
            authorized_builders = sp.set(["consortium"])
        ),
        "transfer": sp.record(
            template_id = 0,
            authorized_builders = sp.set(["consortium"])
        ),
        "set_pause": sp.record(
            template_id = 0,
            authorized_builders = sp.set(["consortium"])
        ),
        "set_metadata": sp.record(
            template_id = 0,
            authorized_builders = sp.set(["consortium"])
        ),
        "update_groups": sp.record(
            template_id = 0,
            authorized_builders = sp.set(["consortium"])
        ),
        "update_templates": sp.record(
            template_id = 0,
            authorized_builders = sp.set(["consortium"])
        ),
        "update_rules": sp.record(
            template_id = 0,
            authorized_builders = sp.set(["consortium"])
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
