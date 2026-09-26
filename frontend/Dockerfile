# ==========================================
# 1. Estágio de Compilação (Build Stage)
# ==========================================
FROM node:20-alpine AS builder

WORKDIR /app

# Instala dependências aproveitando cache de camadas
COPY package*.json ./
RUN npm ci

# Copia código-fonte e compila para produção
COPY . .
RUN npm run build

# ==========================================
# 2. Estágio de Servidor Web (Nginx Alpine)
# ==========================================
FROM nginx:alpine

# Remove configuração padrão do Nginx
RUN rm -rf /usr/share/nginx/html/* /etc/nginx/conf.d/default.conf

# Copia build estático gerado pelo Vite
COPY --from=builder /app/dist /usr/share/nginx/html

# Copia configuração otimizada do Nginx com suporte a SPA e proxy
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
