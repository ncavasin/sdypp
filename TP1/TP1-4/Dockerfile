FROM node:14-alpine
# create root application folder
WORKDIR /
# copy configs to /app folder
COPY package*.json ./
COPY tsconfig.json ./
# copy public code to /public folder
COPY public /public
# copy source code to /src folder
COPY src /src
# Install dependencies
RUN npm install
# Build the app
RUN npm run build

EXPOSE 3001

CMD [ "node", "./dist/index.js" ]