#!/usr/bin/env python3
# -*- coding:utf-8 -*-

import json
import subprocess
from pathlib import Path

import rich
import yaml
from clk.decorators import (
    option,
    group,
)
from clk.lib import (
    call,
    safe_check_output,
    read
)
from clk.log import get_logger
from prompt_toolkit import prompt
from prompt_toolkit.completion import WordCompleter
from prompt_toolkit.validation import Validator
from rich.panel import Panel

LOGGER = get_logger(__name__)


def safe_json_read_array(path):
    r = read(path)
    if r:
        return json.loads(r)
    else:
        LOGGER.debug(
            f"No content for path={path}, returning empty array in place")
        return []


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


def tzc_prompt(msg, choices):
    return prompt(msg,
                  completer=WordCompleter(choices),
                  validator=validator(choices),
                  mouse_support=True)


def echo_invalid(msg):
    rich.print("[bold red] :no_entry: [/bold red]" + msg)


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
    def find_first_account_by_name(name):
        contracts = safe_json_read_array(TezosClient.public_keys_hashs_path)
        return next(filter(lambda x: x['name'] == name, contracts))

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


@rsa.command(name="generate")
def generate_rsa():
    """List the keys locally available"""
    call('openssl genrsa -out keypair.pem 1024'.split())
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
    with open("../sita/helm/sita/values-dev.yaml", "r") as helm_file:
        try:
            helm_content = yaml.safe_load(helm_file)
            collaborate_root_field = helm_content
            if 'collaborate-dapp' in helm_content:
                collaborate_root_field = helm_content['collaborate-dapp']
            collaborate_root_field['api']['publicKey'] = public_key
            collaborate_root_field['api']['privateKey'] = private_key

        except yaml.YAMLError as exc:
            print(exc)

    with open("../sita/helm/sita/values-dev.yaml", "w") as helm_file:
        yaml.dump(helm_content, helm_file, default_flow_style=False,
                  allow_unicode=True)


@collaborate.group(name='business-data')
def bd():
    """Commands to play with business-data smart-contracts"""


@bd.command(name="update-organization")
@option('--contract', help='The contract alias or address to call')
@option('--sender', help='The account alias to use as transaction sender')
def bd_update_organization(contract, sender):
    contract_names = TezosClient.get_contract_names()
    if not contract:
        contract = tzc_prompt('Smart contract > ', contract_names)

    account_names = TezosClient.get_account_names()
    if not sender:
        sender = tzc_prompt('Sender > ', account_names)

    entry_point_params = '{Right (Pair (Pair "<address>" "<encryption_key>") (Pair "<name>" {<roles>}))}'
    org = {
        "legal_name": "SITA",
        "roles": ["0", "1"],
        "address": "tz1T8T3AQdH2tUkkh4FRUkjW3AsQqK8RuBcu",
        "encryption_key": "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAzEi+JDOxh+ENuGfl7hpGlRp/iSwG7L2Z1pRhfTt4vDAqi/bN2T/BhjzMhYZrYLQXi3CvYC3WOGqKj94Hi3SgYqkEZ1c1MihE4+7bN+DrCR11YItCVPL2Oac99mO/3MqxMajH/mfJAIZcy8P5Ey6hFLnGbdtW6vXXc25BLhoJoWLxgkh5I/DvBK4p0zfwqRUokEsy5Fcndy81DZUcGnqIhaL7Y48Sdhe9K3tEdZWoQAVZIgloZAxfaFIryYOqOS6kJxzItQRDesl7nIGnQUWoW0Qwh3q+GAMeYllxzITMf+Ti++kQOVVVZvyoJO+dRMncOqL496SmFGcp5jpKZkNh6wIDAQAB"
    }
    entry_point_params = entry_point_params.replace('<address>', org['address'])
    entry_point_params = entry_point_params.replace('<encryption_key>', org['encryption_key'])
    entry_point_params = entry_point_params.replace('<name>', org['legal_name'])
    entry_point_params = entry_point_params.replace('<roles>', '; '.join(org['roles']))
    print(entry_point_params)

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
    print(entry_point_params)

    stream_command([
        "tezos-client",
        "call", contract,
        "from", sender,
        "--entrypoint", "update_organizations",
        "--arg", entry_point_params,
        "--burn-cap", "0.5"
    ])
