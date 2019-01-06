FROM node:8 AS NODE_BUILD

RUN mkdir /tmp/npm-global && npm config set prefix /tmp/npm-global
RUN npm install -g elm@0.18.0-exp5 --unsafe-perm=true --allow-root
COPY src /tmp/src
COPY static /tmp/static
COPY styles /tmp/styles
COPY elm-package.json /tmp/elm-package.json
COPY Main.elm /tmp/Main.elm
COPY package.json /tmp/package.json
COPY submission.less /tmp/submission.less

WORKDIR /tmp/
RUN export PATH=/tmp/npm-global/bin:$PATH && npm install && npm run build

FROM kyma/docker-nginx
COPY --from=NODE_BUILD /tmp/dist/ /var/www
CMD 'nginx'