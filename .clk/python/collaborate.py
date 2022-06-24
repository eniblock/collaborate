#!/usr/bin/env python3
# -*- coding:utf-8 -*-

import click
import json
import glob
import readline
import subprocess
from distlib.compat import raw_input
from pathlib import Path
from os.path import exists
from shlex import split
import rich
from rich import print_json
import yaml
from clk.decorators import (
    argument,
    option,
    group,
)
from clk.lib import (
    call,
    check_output,
    safe_check_output,
    read
)
from clk.log import get_logger
from prompt_toolkit import prompt
from prompt_toolkit.completion import WordCompleter
from prompt_toolkit.validation import Validator
from rich.panel import Panel

LOGGER = get_logger(__name__)


def echo_invalid(msg):
    rich.print("[bold red] :no_entry: [/bold red]" + msg)


def path_complete(text, state):
    return (glob.glob(text + '*') + [None])[state]


def prompt_path_autocomplete(msg):
    readline.set_completer_delims(' \t\n;')
    readline.parse_and_bind("tab: complete")
    readline.set_completer(path_complete)
    return raw_input(f'{msg} > ')


def safe_json_read_array(path):
    r = read(path)
    if r:
        return json.loads(r)
    else:
        LOGGER.debug(
            f"No content for path={path}, returning empty array in place")
        return []


def tzc_prompt(msg, choices):
    return prompt(msg,
                  completer=WordCompleter(choices),
                  validator=validator(choices),
                  mouse_support=True)


def stream_command(command):
    process = subprocess.Popen(command,
                               stdout=subprocess.PIPE,
                               stderr=subprocess.PIPE)
    while True:
        output = process.stdout.readline()
        if not output and process.poll() is not None:
            break
        if output:
            rich.print(output.decode("utf-8").rstrip())
    rc = process.poll()

    if rc:
        rich.print(
            Panel(
                ":no_entry: " +
                f"{' '.join(command)} :\n\n{process.stderr.read().decode('utf-8')}"
            ))
    else:
        rich.print(
            Panel('[bold green]:heavy_check_mark: [/bold green] ' +
                  ' '.join(command)))

    return rc


def validator(valids, error='Not a valid value'):
    return Validator.from_callable(lambda x: x in valids,
                                   error_message=error,
                                   move_cursor_to_end=True)


class SmartPyCli:
    clk_install_script_path = Path(__file__).parent.parent / 'install-smartpy.sh'
    base_dir = str(Path.home()) + '/smartpy-cli/'
    script_path = base_dir + 'SmartPy.sh'


class TezosClient:
    base_dir = str(Path.home()) + '/.tezos-client/'
    contracts_path = base_dir + 'contracts'
    secret_keys_path = base_dir + 'secret_keys'
    public_keys_hashs_path = base_dir + 'public_key_hashs'
    public_keys_path = base_dir + 'public_keys'
    rpcs = [
        'https://hangzhounet.api.tez.ie', 'https://ithacanet.ecadinfra.com',
        'https://hangzhounet.smartpy.io/', 'https://ithacanet.smartpy.io/'
    ]

    @staticmethod
    def get_account_names():
        contracts = safe_json_read_array(TezosClient.public_keys_hashs_path)
        return list(map(lambda x: x['name'], contracts))

    @staticmethod
    def find_first_account_address_by_name(name):
        contracts = safe_json_read_array(TezosClient.public_keys_hashs_path)
        return next(filter(lambda x: x['name'] == name, contracts))['value']

    @staticmethod
    def get_contract_names():
        contracts = safe_json_read_array(TezosClient.contracts_path)
        return list(map(lambda x: x['name'], contracts))


@group()
def collaborate():
    """Commands to play with collaborate"""


@collaborate.group()
def rsa():
    """Commands to play with rsa"""


@collaborate.command(name="activate")
@option("--activation-key", type=click.Path(exists=True),
        help="The faucet activation key JSON file")
@option("--alias", prompt=True,
        help="The alias to for later reference to the activated account")
@option("--reveal", is_flag=True,
        help="Make the reveal transaction for the created account")
def account_activate(activation_key, alias, reveal):
    """Activate an account from a faucet activation key JSON file"""
    if not activation_key:
        activation_key = prompt_path_autocomplete('JSON activation key file')
    stream_command(
        split(f'tezos-client activate account {alias} with {activation_key}'))

    if reveal:
        click.get_current_context().invoke(account_reveal, alias=alias)


@collaborate.command(name="reveal")
@option("--alias", help="The account alias to reveal the public key")
def account_reveal(alias):
    """Reveal an account public key"""

    if not alias:
        accounts = safe_json_read_array(TezosClient.public_keys_path)
        names = list(map(lambda x: x['name'], accounts))
        alias = prompt("Account alias to reveal >",
                       completer=WordCompleter(names),
                       validator=validator(names, 'Not a valid alias'),
                       mouse_support=True)
    stream_command(split(f'tezos-client reveal key for {alias}'))


@collaborate.command(name="init-organization")
@option("--from-alias", help="The account alias to import")
@option("--force", is_flag=True, help="Overwrite the account if exists")
def init_organization(from_alias, force):
    """Initialize a new organization config
  [ ] Wallet activation (from faucet)
  - Access request private and public keys
  - Roles
  - Name
  """
    # TODO
    pass


@rsa.command(name="generate")
def generate_rsa():
    """List the keys locally available"""
    call('openssl genrsa -out keypair.pem 2048'.split())
    private_key = read('keypair.pem')
    private_key = private_key.replace('-----BEGIN PRIVATE KEY-----', '')
    private_key = private_key.replace('-----END PRIVATE KEY-----', '')
    private_key = private_key.replace('\n', '')
    # print(private_key)

    public_key = safe_check_output('openssl rsa -in keypair.pem -pubout'.split())
    public_key = public_key.replace('-----BEGIN PUBLIC KEY-----', '')
    public_key = public_key.replace('-----END PUBLIC KEY-----', '')
    public_key = public_key.replace('\n', '')
    # print(public_key)

    call('rm keypair.pem'.split())

    helm_content = {}
    # with open("../sita/helm/sita/values-dev.yaml", "r") as helm_file:
    #     try:
    #         helm_content = yaml.safe_load(helm_file)
    #         collaborate_root_field = helm_content
    #         if 'collaborate-dapp' in helm_content:
    #             collaborate_root_field = helm_content['collaborate-dapp']
    #         collaborate_root_field['api']['publicKey'] = public_key
    #         collaborate_root_field['api']['privateKey'] = private_key
    #
    #     except yaml.YAMLError as exc:
    #         print(exc)
    #
    # with open("../sita/helm/sita/values-dev.yaml", "w") as helm_file:
    #     yaml.dump(helm_content, helm_file, default_flow_style=False,
    #               allow_unicode=True)
    print(f'public_key= {public_key}\n')
    print(f'private_key= {private_key}')


@collaborate.group(name='business-data')
def bd():
    """Commands to play with business-data smart-contracts"""


@collaborate.command()
def hexencode(str):
    res = str.encode('utf-8').hex()
    return "0x" + res


@bd.command(name="update-organization")
@option('--contract', help='The contract alias or address to call')
@option('--sender', help='The account alias to use as transaction sender')
@option('--organizations-path',
        help='The file containing the organization configurations')
@option('--organization-id',
        help='The id of the organization to import from the organizations-path')
def bd_update_organization(contract, sender, organizations_path,
                           organization_id):
    """Add or update (when the wallet address is already known) an organization"""
    contract_names = TezosClient.get_contract_names()
    if not contract:
        contract = tzc_prompt('Smart contract > ', contract_names)

    account_names = TezosClient.get_account_names()
    if not sender:
        sender = tzc_prompt('Sender > ', account_names)

    if not organizations_path or not exists(organizations_path):
        readline.set_completer_delims(' \t\n;')
        readline.parse_and_bind("tab: complete")
        readline.set_completer(path_complete)
        organizations_path = raw_input('Organizations path > ')
    organizations = safe_json_read_array(organizations_path)

    if not organization_id:
        organizations_ids = list(map(lambda x: x['id'], organizations))
        organization_id = tzc_prompt('Organization Id > ', organizations_ids)
    organization = next(
        filter(lambda x: x['id'] == organization_id, organizations))

    entry_point_params = '{Right (Pair (Pair "<address>" "<encryption_key>") (Pair "<name>" {<roles>}))}'
    entry_point_params = entry_point_params.replace('<address>',
                                                    organization['address'])
    entry_point_params = entry_point_params.replace('<encryption_key>',
                                                    organization[
                                                        'encryption_key'])
    entry_point_params = entry_point_params.replace('<name>',
                                                    organization['legal_name'])
    entry_point_params = entry_point_params.replace('<roles>', '; '.join(
        organization['roles']))

    stream_command([
        "tezos-client",
        "call", contract,
        "from", sender,
        "--entrypoint", "update_organizations",
        "--arg", entry_point_params,
        "--burn-cap", "0.5"
    ])


@bd.command(name="remove-organization")
@option('--contract', help='The contract alias or address to call')
@option('--sender', help='The account alias to use as transaction sender')
@option('--remove-address', help='The organization to remove')
def bd_remove_organization(contract, sender, remove_address):
    """Remove an organization based on the provided wallet address"""
    contract_names = TezosClient.get_contract_names()
    if not contract:
        contract = tzc_prompt('Smart contract > ', contract_names)

    account_names = TezosClient.get_account_names()
    if not sender:
        sender = tzc_prompt('Sender > ', account_names)

    if not remove_address:
        remove_address = prompt('Address to remove > ')

    entry_point_params = '{Left "<address>"}'
    entry_point_params = entry_point_params.replace('<address>', remove_address)

    stream_command([
        "tezos-client",
        "call", contract,
        "from", sender,
        "--entrypoint", "update_organizations",
        "--arg", entry_point_params,
        "--burn-cap", "0.5"
    ])


@bd.command(name="set-administrator")
@option('--contract', help='The contract alias or address to call')
@option('--sender', help='The account alias to use as transaction sender')
@option('--administrator-address', help='The administrator address')
def bd_set_administrator(contract, sender, administrator_address):
    """Set the contract administrator (sender should be the current administrator)"""
    contract_names = TezosClient.get_contract_names()
    if not contract:
        contract = tzc_prompt('Smart contract > ', contract_names)

    account_names = TezosClient.get_account_names()
    if not sender:
        sender = tzc_prompt('Sender > ', account_names)

    if not administrator_address:
        name = tzc_prompt('Administrator address to set > ', account_names)
        administrator_address = TezosClient.find_first_account_address_by_name(name)

    entry_point_params = '"<address>"'
    entry_point_params = entry_point_params.replace('<address>',
                                                    administrator_address)

    stream_command([
        "tezos-client",
        "call", contract,
        "from", sender,
        "--entrypoint", "set_administrator",
        "--arg", entry_point_params,
        "--burn-cap", "0.5"
    ])


@collaborate.group(name='test')
def test():
    """Commands to test deployments state"""


def get_access_token(dappBaseURL):
    raw_result = check_output(["curl", "-k", "--location", "--request", "POST",
                               f"{dappBaseURL}/auth/realms/collaborate-dapp/protocol/openid-connect/token",
                               "--header",
                               "'Content-Type: application/x-www-form-urlencoded",
                               "--data-urlencode", "grant_type=password",
                               "--data-urlencode", "username=sam",
                               "--data-urlencode", "password=admin",
                               "--data-urlencode", "client_id=frontend"])
    json_result = json.loads(raw_result)
    return json_result['access_token']


@test.command(name="organization")
@argument('dapp-url', help='The URL of the Collaborate instance to check')
def test_organization(dapp_url):
    access_token = get_access_token(dapp_url)
    raw_organization = check_output(["curl", "-k", "--location", "--request", "GET",
                                     f"{dapp_url}/api/v1/organizations/current",
                                     "--header", f"Authorization: Bearer {access_token}"])
    organization = json.loads(raw_organization)
    print(raw_organization)
    print('address' in organization)
