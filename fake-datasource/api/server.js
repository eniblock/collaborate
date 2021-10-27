const jsonServer = require("json-server");
const hal = require("hal");
const server = jsonServer.create();
const middlewares = jsonServer.defaults();
const jwt = require("jsonwebtoken");
const keycloakCerts = require("get-keycloak-public-key");
const fs = require("fs");

const DB_PATH = process.env.DB_PATH || "db.json";
const URL = process.env.WEB_URL || "http://localhost:3000";
const IAM_URL = process.env.IAM_URL || "http://iam:8080";

console.log("IAM URL =" + IAM_URL);
const router = jsonServer.router(DB_PATH);
const kc = new keycloakCerts(IAM_URL, "datasource");

server.use(middlewares);
server.get("/documents/:id/download", (req, res) => {
  var resource = router.db.get("documents").find({ id: req.params.id }).value();

  auth(req, res, resource.scope, function () {
    fs.writeFile(
      "metadata" + req.params.id,
      JSON.stringify(resource),
      function (err) {
        if (err) {
          return console.log(err);
        }

        res.download("metadata" + req.params.id);
      }
    );
  });
});

server.use(router);

auth = async function (req, res, scope, next) {
  const authHeader = req.headers.authorization;
  console.log("Authenticating");

  if (authHeader) {
    const token = authHeader.split(" ")[1];

    // decode the token without verification to have the kid value
    const kid = jwt.decode(token, { complete: true }).header.kid;

    // fetch the PEM Public Key
    let onKcFetchSuccess = (publicKey) => {
      console.log(publicKey);
      if (publicKey) {
        jwt.verify(token, publicKey, (err, user) => {
          if (err) {
            console.error("Failed verifying jwt:", err);
            return res.sendStatus(403);
          }

          let scopes = user.scope.split(" ");
          if (!scopes.includes(scope)) {
            console.log("user has not expected scope=" + scope);
            return res.sendStatus(403);
          }
          req.user = user;
          next();
        });
      } else {
        return res.sendStatus(401);
      }
    };
    kc.fetch(kid).then(onKcFetchSuccess, (error) => console.error(error));
  } else {
    return res.sendStatus(401);
  }
};

router.render = async function (req, res) {
  console.log(req.route);
  console.log(req.path);

  let scope = "";
  let resource;
  if (req.path === "/documents") {
    scope = "metadata";
    resource = new hal.Resource({}, `${URL}${req.path}`);
    let rows = [];

    for (const row of res.locals.data) {
      var embeddedResource = new hal.Resource(
        row,
        `${URL}/documents/${row.id}`
      );
      embeddedResource.link("download", `${URL}/documents/${row.id}/download`);
      rows.push(embeddedResource);
    }
    resource.embed("metadata", rows);
  }

  if (req.route.path === "/:id") {
    scope = res.locals.data.scope;
    resource = new hal.Resource(res.locals.data, `${URL}${req.path}`);
    resource.link(
      "download",
      `${URL}/documents/${res.locals.data.id}/download`
    );
  }

  auth(req, res, scope, function () {
    res.setHeader("Content-Type", "application/hal+json");
    res.jsonp(resource);
  });
};

server.listen(3000, () => {
  console.log("JSON Server is running");
});
