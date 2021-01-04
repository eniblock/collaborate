const jsonServer = require('json-server')
const hal = require('hal');
const server = jsonServer.create()
const router = jsonServer.router('db.json')
const middlewares = jsonServer.defaults()
const jwt = require('jsonwebtoken');
const keycloakCerts = require('get-keycloak-public-key');
const kc = new keycloakCerts('http://datasource-iam:8080', 'datasource');

server.use(middlewares)
server.use(async (req, res, next) => {
    const authHeader = req.headers.authorization;
    console.log(authHeader);
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
                console.log(user);
                req.user = user;
                next();
            });
        } else {
            res.sendStatus(401);
        }
    } else {
        return res.sendStatus(401);
    }
})

server.use(router)

router.render = (req, res) => {
    if (Array.isArray(res.locals.data)) {
        var resource = new hal.Resource({}, req.path);
        var rows = [];

        for (const row of res.locals.data) {
            var embeddedResource = new hal.Resource(row, "/metadata/" + row.id);

            rows.push(embeddedResource);
        }

        resource.embed("metadata", rows);
    } else {
        var resource = new hal.Resource(res.locals.data, req.path);
    }

    res.jsonp(resource);
}

server.listen(3000, () => {
    console.log('JSON Server is running');
})