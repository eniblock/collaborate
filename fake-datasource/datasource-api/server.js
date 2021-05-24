const jsonServer = require('json-server')
const hal = require('hal');
const server = jsonServer.create()
const middlewares = jsonServer.defaults()
const jwt = require('jsonwebtoken');
const keycloakCerts = require('get-keycloak-public-key');
const fs = require('fs');

const DB_PATH = process.env.DB_PATH || 'db.json';
const URL = process.env.WEB_URL || 'http://localhost:3000';
const IAM_URL = process.env.IAM_URL || 'http://datasource-iam:8080';

const router = jsonServer.router(DB_PATH)
const kc = new keycloakCerts(IAM_URL, 'datasource');

server.use(middlewares);
server.get('/metadata/:id/download', (req, res) => {
    var resource = router.db.get('metadata')
                     .find({ id: req.params.id })
                     .value();

    auth(req, res, resource.scope, function () {
        fs.writeFile("metadata" + req.params.id, JSON.stringify(resource), function (err) {
            if (err) return console.log(err);

            res.download("metadata" + req.params.id);
        });
    });
})

server.use(router);

auth = async function (req, res, scope, next) {
    const authHeader = req.headers.authorization;

    if (authHeader) {
        const token = authHeader.split(' ')[1];

        // decode the token without verification to have the kid value
        const kid = jwt.decode(token, { complete: true }).header.kid;

        // fetch the PEM Public Key
        const publicKey = await kc.fetch(kid);

        if (publicKey) {
            jwt.verify(token, publicKey, (err, user) => {
                if (err) {
                    return res.sendStatus(403);
                }

                var scopes = user.scope.split(' ');

                if (!scopes.includes(scope)) {
                    return res.sendStatus(403);
                }

                req.user = user;

                next()
            });
        } else {
            return res.sendStatus(401);
        }
    } else {
        return res.sendStatus(401);
    }
}

router.render = async function (req, res) {
    console.log(req.route);
    console.log(req.path);

    if (req.path == '/metadata') {
        var scope = "metadata";
        var resource = new hal.Resource({}, `${URL}${req.path}`);
        var rows = [];

        for (const row of res.locals.data) {
            var embeddedResource = new hal.Resource(row, `${URL}/metadata/${row.id}`);

            embeddedResource.link('download', `${URL}/metadata/${row.id}/download`);

            rows.push(embeddedResource);
        }

        resource.embed("metadata", rows);
    }

    if (req.route.path == '/:id') {
        var scope = res.locals.data.scope;

        var resource = new hal.Resource(res.locals.data, `${URL}${req.path}`);

        resource.link('download', `${URL}/metadata/${res.locals.data.id}/download`);
    }

    auth(req, res, scope, function () {
		res.setHeader('Content-Type', 'application/hal+json');

        res.jsonp(resource);
    });
}

server.listen(3000, () => {
    console.log('JSON Server is running');
})