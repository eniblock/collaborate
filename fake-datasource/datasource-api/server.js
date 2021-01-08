const jsonServer = require('json-server')
const hal = require('hal');
const server = jsonServer.create()
const router = jsonServer.router('db.json')
const middlewares = jsonServer.defaults()
const jwt = require('jsonwebtoken');
const keycloakCerts = require('get-keycloak-public-key');
const kc = new keycloakCerts('http://datasource-iam:8080', 'datasource');

server.use(middlewares)
server.use(router)

router.render = async function (req, res) {
    const authHeader = req.headers.authorization;

    if (Array.isArray(res.locals.data)) {
        var scope = "list";
        var resource = new hal.Resource({}, req.path);
        var rows = [];

        for (const row of res.locals.data) {
            var embeddedResource = new hal.Resource(row, "/data/" + row.id);

            rows.push(embeddedResource);
        }

        resource.embed("data", rows);
    } else {
        var scope = res.locals.data.type;

        var resource = new hal.Resource(res.locals.data, req.path);
    }

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

                res.jsonp(resource);
            });
        } else {
            return res.sendStatus(401);
        }
    } else {
        return res.sendStatus(401);
    }
}

server.listen(3000, () => {
    console.log('JSON Server is running');
})