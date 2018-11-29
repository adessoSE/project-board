### STAGE 1: Build ###
# We label our stage as ‘node’
FROM node:11.3.0 as node

COPY package.json ./
COPY yarn.lock ./

## Storing node modules on a separate layer will prevent unnecessary npm installs at each build
RUN yarn install && mkdir /ng-app && mv ./node_modules ./ng-app

WORKDIR /ng-app

COPY . .

## Build the angular app in production mode and store the artifacts in dist folder
RUN yarn run build --prod

### STAGE 2: Setup ###
FROM nginx:1.14.1-alpine

## Copy our default nginx config
COPY ./nginx.conf /etc/nginx/conf.d/

## Remove default nginx website
RUN rm -rf /usr/share/nginx/html/*

COPY --from=node /ng-app/dist/project-board-app /usr/share/nginx/html
