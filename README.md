# ✂️ AGS Unissex – Software de Organização para Barbearia / Cabeleireiro 💈

## 📖 Sobre o Projeto

O **AGS Unissex** é um software desenvolvido para facilitar a administração de uma barbearia e salão de cabeleireiro. Utilizando **Java (JavaFX)**, o sistema oferece funcionalidades essenciais para o Gerenciamento de **Clientes**, **Serviços** e **Agendamentos**. Com uma interface intuitiva, proporcionando:

- 📅 **Sistema Simples de Agendamento**
- 💇‍♂️ **Gerenciamento de Tipos de Cortes e Serviços**
- 💰 **Tela de Finanças para Controle Financeiro**

O objetivo é trazer praticidade e organização para o dia a dia, centralizando as informações mais importantes em um só lugar!

---

## 🚀 Tecnologias Utilizadas

| 💻 Tecnologia  | 📝 Descrição                                  |
|:--------------:|:---------------------------------------------|
| 🧑‍💻 IntelliJ  | IDE Utilizada para o Desenvolvimento         |
| 🎨 JavaFX      | Framework para Criação de Interfaces Gráficas |
| ☕ Java         | Linguagem Principal do Projeto                |
| 🛠️ Gradle      | Gerenciador de Dependências e Build           |
| 🗄️ PostgreSQL  | Banco de Dados Relacional                     |
| 🐳 Docker      | Containerização e Fácil Implantação           |

---

## 💡 Funcionalidades

- Cadastro e Gerenciamento de Clientes
- Registro de Serviços e Tipos de Corte
- Agendamento de Horários
- Controle Financeiro Integrado

---

## 🐳 Subindo o Banco de Dados com Docker + Compose

Siga os passos abaixo para configurar e rodar o banco de dados utilizando **Docker** e **Docker Compose**.  

---

### 1️⃣ Instalar o Docker

Baixe e instale o Docker no seu sistema:  
🔗 [**Download do Docker**](https://www.docker.com/)

> 💡 **Dica:** Certifique-se de que o Docker esteja rodando após a instalação.

---

### 2️⃣ Entrar no Projeto

Abra o **terminal/CLI** e navegue até a pasta do seu projeto:  

```bash
cd caminho/do/seu/projeto
```
### 3️⃣ Preparar o arquivo `.env`

Crie um arquivo `.env` na raiz do projeto com as credenciais do banco (o `docker-compose` irá lê-lo automaticamente):

```env
POSTGRES_USER=seu_usuario
POSTGRES_PASSWORD=sua_senha
POSTGRES_DB=nome_do_banco
```
> ⚠️ Não compartilhe esse arquivo em repositórios públicos — adicione-o ao .gitignore.

### 4️⃣ Subir o container do banco

No terminal, dentro da pasta do projeto, execute:

```bash
docker compose up -d
```

Isso criará e iniciará o container do banco usando as configurações do `docker-compose.yml

---

## 👨‍💻 Autor

Desenvolvido por [badpccc](https://github.com/badpccc).<br> 
Estudante de Sistemas de Informação - UniRios

---
