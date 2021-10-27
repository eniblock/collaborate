FROM node:14.15.3-alpine3.12 AS builder

WORKDIR /usr/src/app

COPY package*.json ./

RUN npm install

COPY . .

EXPOSE 3000

FROM builder as dev
CMD [ "node", "server.js", "--inspect" ]

FROM dev
CMD [ "node", "server.js" ]
