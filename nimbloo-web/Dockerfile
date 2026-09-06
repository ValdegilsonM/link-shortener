# ETAPA 1: Construção (Build)
# Usamos o Node.js para instalar as dependências e gerar o HTML/CSS/JS final
FROM node:20-alpine AS builder
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

# ETAPA 2: Servidor Web (Nginx)
FROM nginx:alpine

# Copiamos o nosso arquivo de configuração personalizado substituindo o padrão do Nginx
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Copiamos os arquivos gerados no build para a pasta padrão do Nginx
COPY --from=builder /app/dist /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]