# Stage 1
FROM node:8.11.2-alpine as node

WORKDIR /usr/src/app

COPY package.json ./
COPY yarn.lock ./

RUN yarn install

COPY . .

RUN yarn run build --prod

# Stage 2
FROM nginx:1.13.12-alpine

COPY --from=node /usr/src/app/dist/project-board-app /usr/share/nginx/html
COPY ./nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 4200
