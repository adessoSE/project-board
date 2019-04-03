# Use the latest nginx alpine image
FROM nginx:alpine

# Copy nginx configuration file
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Set working directory to nginx static resource directory
WORKDIR /usr/share/nginx/html/

# Copy the production/development build
COPY dist/project-board-app .

