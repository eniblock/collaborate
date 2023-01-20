# see: https://smartpy.io/reference.html
import smartpy as sp

organization_value_type = sp.TRecord(
    roles = sp.TSet(sp.TNat),  # organization role (1: DSP, 2: BSP)
    legal_name = sp.TString,
    address = sp.TAddress,
    encryption_key = sp.TString, # TODO this can be off-chain
)

organizations_type = sp.TMap(
    sp.TAddress,
    organization_value_type
)

update_org_type = sp.TList(sp.TVariant(
    update = organization_value_type,
    remove = sp.TAddress
))

class OrganizationRoles:
    BNO = 0 # Business Network Operator
    DSP = 1 # Data Service Provider
    BSP = 2 # Business Service Provider

class OrganizationsYellowPages(sp.Contract):
    def __init__(self, organizations: organizations_type, administrator, golden_token_sc, golden_token_id):
        self.init_type(sp.TRecord(
            organizations = organizations_type,
            administrator = sp.TAddress,
            golden_token_sc = sp.TAddress,
            golden_token_id = sp.TNat
        ))
        self.init(
            organizations = organizations,
            administrator = administrator,
            golden_token_sc = golden_token_sc,
            golden_token_id = golden_token_id
        )

    @sp.entry_point
    def set_administrator(self, params):
        sp.verify(self.data.administrator == sp.sender, message = "403 - Not admin")
        self.data.administrator = params

    @sp.entry_point
    def update_organizations(self, params):
        sp.set_type(params, update_org_type)
        # Authorizations
        is_admin = self.data.administrator == sp.sender
        sp.if ~is_admin:
            sp.verify(self.data.organizations.contains(sp.sender),
                  message = "403 - Sender not allowed")
            sp.verify(self.data.organizations[sp.sender].roles.contains(OrganizationRoles.BNO),
                  message = "403 - Sender not allowed")
        # Business logic
        sp.for updates in params:
            with updates.match_cases() as arg:
                with arg.match("update") as upd:
                    self.data.organizations[upd.address] = upd
                with arg.match("remove") as upd:
                    del self.data.organizations[upd]

    @sp.entry_point
    def update_organizations_golden(self, params):
        sp.set_type(params, update_org_type)
        # Business logic
        sp.for updates in params:
            with updates.match_cases() as arg:
                with arg.match("update") as upd:
                    #self.check_golden_token(upd.address)
                    self.data.organizations[upd.address] = upd
                with arg.match("remove") as upd:
                    #self.check_golden_token(upd)
                    del self.data.organizations[upd]

    @sp.onchain_view(name = "get_org")
    def get_org(self, org_address):
        sp.verify(self.data.organizations.contains(org_address), "UNKNOWN_ORGANIZATION")
        sp.result(self.data.organizations[org_address])

    @sp.onchain_view(name = "is_org")
    def is_org(self, org_address):
        sp.result(self.data.organizations.contains(org_address))

    def check_golden_token(self, address):
        sp.set_type(address, sp.TAddress)
        sp.verify(address == sp.sender, message = "403 - Sender does not own the organization")
        req = sp.record(
            owner = address,
            token_id = self.data.golden_token_id
        )
        balance = sp.compute(sp.view("get_balance", self.data.golden_token_sc, req, t = sp.TNat).open_some("Invalid get_balance view"))
        sp.verify(balance > 0, message = "403 - Sender has no Golden Token")

if "templates" not in __name__:
    def origination():
        ### Origination - deployment target
        origination = OrganizationsYellowPages(
            organizations = sp.map(tkey=sp.TAddress, tvalue=organization_value_type),
            administrator = sp.address("tz1UeuazWZL3HKF8qoa7qBT1h91Vy5Le5akc"),
            golden_token_sc = sp.address("KT1X3c3WX97eZ7K5CKqqFeiamnKfsfDf1EvL"),
            golden_token_id = 0
        )

        sp.add_compilation_target(
            "Organization_Yellow_Pages",
            origination
        )

    @sp.add_test(name = "OrganizationsYellowPages", is_default = True)
    def test():
        origination()

        scenario = sp.test_scenario()
        admin = sp.address("tz1W5ubDUJwpd9Gb94V2YKnZBHggAMMxtbBd")
        golden_token_sc = sp.address("KT1X3c3WX97eZ7K5CKqqFeiamnKfsfDf1EvL")
        golden_token_id = 0

        dsp_org_1 = sp.record(
            legal_name = 'DSPConsortium1',
            address = sp.address('tz1SDYtreHuKGe7QNcZTjKQwfSreLR8JYW6c'),
            encryption_key = 'MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAzEi+JDOxh+ENuGfl7hpGlRp/iSwG7L2Z1pRhfTt4vDAqi/bN2T/BhjzMhYZrYLQXi3CvYC3WOGqKj94Hi3SgYqkEZ1c1MihE4+7bN+DrCR11YItCVPL2Oac99mO/3MqxMajH/mfJAIZcy8P5Ey6hFLnGbdtW6vXXc25BLhoJoWLxgkh5I/DvBK4p0zfwqRUokEsy5Fcndy81DZUcGnqIhaL7Y48Sdhe9K3tEdZWoQAVZIgloZAxfaFIryYOqOS6kJxzItQRDesl7nIGnQUWoW0Qwh3q+GAMeYllxzITMf+Ti++kQOVVVZvyoJO+dRMncOqL496SmFGcp5jpKZkNh6wIDAQAB',
            roles = {1}
        )
        bsp_org_2 = sp.record(
            legal_name = 'BSPConsortium2',
            address = sp.address('tz1PC8dnju6zkgnpFVDCnYnmHXaDTNQoDh9W'),
            encryption_key = 'MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAsCSS6ayF41KEOOxaTVdnO5SulP7EnFFxjs6E7i8HSDxYgoLQTlqPycvp86dcfRwLPtySP1EHHtTKEsQmPnaWA7npBEwkTmg9VkFseetmph6h2GiaCcxhOpRnpYEfCtjlF89OPVZPU3lvIeQCZhud/YaGk/4+8I1ZRHgEwhJXXc3MFr9V71k8jGxj/Sbmy0v5ATzzMmCchi1MGvH9acZy2UUSczO8O7burs5SrRpxY9JmAV/tFy1cnYwsPrs25XklI/x6KS/fZneybEJZ0QHNQLUEkKgqZOeNc7aK8TWX2ZTvjMnCfp1zhR2sFtXNMSja/fA9H/1UcR8j3cu4qaI1ewIDAQAB',
            roles = {2}
        )
        organizations = sp.map(
            {
                dsp_org_1.address: dsp_org_1,
                bsp_org_2.address: bsp_org_2
            }
        )

        c1 = OrganizationsYellowPages(
            organizations = organizations,
            administrator=admin,
            golden_token_sc=golden_token_sc,
            golden_token_id = 0)

        scenario += c1

        ## remove organization
        scenario += c1.update_organizations([sp.variant("remove", bsp_org_2.address)]).run(sender = admin)
        scenario.verify(~ c1.data.organizations.contains(bsp_org_2.address))

        ## add org
        scenario += c1.update_organizations([sp.variant("update", bsp_org_2)]).run(sender = admin)
        scenario.verify(c1.data.organizations.contains(bsp_org_2.address))

        ## update org
        expected_dsp_org_2 = sp.record(
            legal_name = 'U'+bsp_org_2.legal_name,
            address = bsp_org_2.address,
            encryption_key = bsp_org_2.encryption_key,
            roles = {2, 1}
        )
        scenario += c1.update_organizations([sp.variant("update", expected_dsp_org_2)]).run(sender = admin)
        scenario.verify(c1.data.organizations.contains(expected_dsp_org_2.address))
        actual_dsp_org_2 = c1.data.organizations[expected_dsp_org_2.address]
        scenario.verify(actual_dsp_org_2.address == expected_dsp_org_2.address)
        scenario.verify(actual_dsp_org_2.legal_name == expected_dsp_org_2.legal_name)

        ## test onchain views
        view_org1 = sp.view("get_org", c1.address, dsp_org_1.address, t = organization_value_type).open_some("Invalid view")
        scenario.verify(view_org1.address == dsp_org_1.address)
        scenario.verify(view_org1.legal_name == dsp_org_1.legal_name)
        scenario.verify(view_org1.legal_name == dsp_org_1.legal_name)
        scenario.show(view_org1)

        scenario.verify(sp.view("is_org", c1.address, dsp_org_1.address, t = sp.TBool) == sp.some(True))
        scenario.verify(sp.view("is_org", c1.address, expected_dsp_org_2.address, t = sp.TBool) == sp.some(True))
        scenario.verify(sp.view("is_org", c1.address, c1.address, t = sp.TBool) == sp.some(False))
