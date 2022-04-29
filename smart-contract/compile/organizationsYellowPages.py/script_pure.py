import smartpy as sp

organization_value_type = sp.TRecord(
    roles = sp.TSet(sp.TNat),  # organization role (1: DSP, 2: BSP)
    legal_name = sp.TString,
    address = sp.TAddress,
    encryption_key = sp.TString, # TODO this can be off-chain
    metadata = sp.TMap(sp.TString, sp.TBytes)
)

organizations_type = sp.TMap(
    sp.TAddress,
    organization_value_type
)

update_org_type = sp.TList(sp.TVariant(
    update = organization_value_type,
    remove = sp.TAddress
))

class OrganizationsYellowPages(sp.Contract):
    def __init__(self, organizations: organizations_type, administrators):
        self.init_type(sp.TRecord(
            organizations = organizations_type,
            administrators = sp.TSet(sp.TAddress)
        ))
        self.init(
            organizations = organizations,
            administrators = administrators
        )

    @sp.entry_point
    def update_organizations(self, params):
        sp.set_type(params, update_org_type)
        sp.verify(self.data.administrators.contains(sp.sender), "INVALID_SENDER")
        with sp.for_('updates', params) as updates:
            with updates.match_cases() as arg:
                with arg.match("update") as upd:
                    self.data.organizations[upd.address] = upd
                with arg.match("remove") as upd:
                    del self.data.organizations[upd]

    @sp.onchain_view(name = "get_org")
    def get_org(self, org_address):
        sp.verify(self.data.organizations.contains(org_address), "UNKNOWN_ORGANIZATION")
        sp.result(self.data.organizations[org_address])

    @sp.onchain_view(name = "is_org")
    def is_org(self, org_address):
        sp.result(self.data.organizations.contains(org_address))

if "templates" not in __name__:
    @sp.add_test(name = "OrganizationsYellowPages")
    def test():
        scenario = sp.test_scenario()
        admin = sp.address("tz1W5ubDUJwpd9Gb94V2YKnZBHggAMMxtbBd")

        orga_DSPConsortium1 = sp.record(
            legal_name = 'DSPConsortium1',
            address = sp.address('tz1WpmFuSZfuNS7XDKwDZxX3QhSNUneTkiTv'),
            encryption_key = 'MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAzEi+JDOxh+ENuGfl7hpGlRp/iSwG7L2Z1pRhfTt4vDAqi/bN2T/BhjzMhYZrYLQXi3CvYC3WOGqKj94Hi3SgYqkEZ1c1MihE4+7bN+DrCR11YItCVPL2Oac99mO/3MqxMajH/mfJAIZcy8P5Ey6hFLnGbdtW6vXXc25BLhoJoWLxgkh5I/DvBK4p0zfwqRUokEsy5Fcndy81DZUcGnqIhaL7Y48Sdhe9K3tEdZWoQAVZIgloZAxfaFIryYOqOS6kJxzItQRDesl7nIGnQUWoW0Qwh3q+GAMeYllxzITMf+Ti++kQOVVVZvyoJO+dRMncOqL496SmFGcp5jpKZkNh6wIDAQAB',
            roles = {1},
            metadata = {}
        )
        orga_BSPConsortium2 = sp.record(
            legal_name = 'BSPConsortium2',
            address = sp.address('tz1XDwkYzcxqswBhQDd1QxiBrA53J5bZzHg2'),
            encryption_key = 'MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAsCSS6ayF41KEOOxaTVdnO5SulP7EnFFxjs6E7i8HSDxYgoLQTlqPycvp86dcfRwLPtySP1EHHtTKEsQmPnaWA7npBEwkTmg9VkFseetmph6h2GiaCcxhOpRnpYEfCtjlF89OPVZPU3lvIeQCZhud/YaGk/4+8I1ZRHgEwhJXXc3MFr9V71k8jGxj/Sbmy0v5ATzzMmCchi1MGvH9acZy2UUSczO8O7burs5SrRpxY9JmAV/tFy1cnYwsPrs25XklI/x6KS/fZneybEJZ0QHNQLUEkKgqZOeNc7aK8TWX2ZTvjMnCfp1zhR2sFtXNMSja/fA9H/1UcR8j3cu4qaI1ewIDAQAB',
            roles = {2},
            metadata = {}
        )
        organizations = sp.map(
            {
                orga_DSPConsortium1.address: orga_DSPConsortium1,
                orga_BSPConsortium2.address: orga_BSPConsortium2
            }
        )

        c1 = OrganizationsYellowPages(organizations, sp.set([admin]))

        scenario += c1

        ## remove organization
        scenario += c1.update_organizations([sp.variant("remove", orga_BSPConsortium2.address)]).run(sender = admin)
        scenario.verify(~ c1.data.organizations.contains(orga_BSPConsortium2.address))

        ## add org
        scenario += c1.update_organizations([sp.variant("update", orga_BSPConsortium2)]).run(sender = admin)
        scenario.verify(c1.data.organizations.contains(orga_BSPConsortium2.address))

        ## update org
        updated_orga_BSPConsortium2 = sp.record(
            legal_name = 'UPDBSPConsortium2',
            address = sp.address('tz1XDwkYzcxqswBhQDd1QxiBrA53J5bZzHg2'),
            encryption_key = 'MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAsCSS6ayF41KEOOxaTVdnO5SulP7EnFFxjs6E7i8HSDxYgoLQTlqPycvp86dcfRwLPtySP1EHHtTKEsQmPnaWA7npBEwkTmg9VkFseetmph6h2GiaCcxhOpRnpYEfCtjlF89OPVZPU3lvIeQCZhud/YaGk/4+8I1ZRHgEwhJXXc3MFr9V71k8jGxj/Sbmy0v5ATzzMmCchi1MGvH9acZy2UUSczO8O7burs5SrRpxY9JmAV/tFy1cnYwsPrs25XklI/x6KS/fZneybEJZ0QHNQLUEkKgqZOeNc7aK8TWX2ZTvjMnCfp1zhR2sFtXNMSja/fA9H/1UcR8j3cu4qaI1ewIDAQAB',
            roles = {2, 1},
            metadata = {}
        )
        scenario += c1.update_organizations([sp.variant("update", updated_orga_BSPConsortium2)]).run(sender = admin)
        scenario.verify(c1.data.organizations.contains(updated_orga_BSPConsortium2.address))
        scenario.verify(c1.data.organizations[orga_BSPConsortium2.address].address == updated_orga_BSPConsortium2.address)
        scenario.verify(c1.data.organizations[orga_BSPConsortium2.address].legal_name == "UPDBSPConsortium2")

        ## test onchain views
        view_org1 = sp.view("get_org", c1.address, orga_DSPConsortium1.address, t = organization_value_type).open_some("Invalid view")
        scenario.verify(view_org1.address == orga_DSPConsortium1.address)
        scenario.verify(view_org1.legal_name == orga_DSPConsortium1.legal_name)
        scenario.verify(view_org1.legal_name == orga_DSPConsortium1.legal_name)
        scenario.show(view_org1)

        scenario.verify(sp.view("is_org", c1.address, orga_DSPConsortium1.address, t = sp.TBool) == sp.some(True))
        scenario.verify(sp.view("is_org", c1.address, updated_orga_BSPConsortium2.address, t = sp.TBool) == sp.some(True))
        scenario.verify(sp.view("is_org", c1.address, c1.address, t = sp.TBool) == sp.some(False))
