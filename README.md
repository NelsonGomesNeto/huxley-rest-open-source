huxley-web
==========

![alt tag](https://codeship.com/projects/104097/status?branch=master)


Projeto web para o The Huxley com a nova interface.


## Configurando o ambiente de desenvolvimento para trabalhar na interface gráfica com Angular.js

### Instalando o Node.js no Ubuntu

* Pré-requisitos
``` bash
    sudo apt-get install curl build-essential
```

* Instalando o Node.js e NPM
``` bash
    echo 'export PATH=$HOME/local/bin:$PATH' >> ~/.bashrc
    . ~/.bashrc
    mkdir ~/local
    mkdir ~/node-latest-install
    cd ~/node-latest-install
    curl http://nodejs.org/dist/node-latest.tar.gz | tar xz --strip-components=1
    ./configure --prefix=~/local
    make install # ok, fine, this step probably takes more than 30 seconds...
    curl https://www.npmjs.org/install.sh | sh
```

### Instalando o Bower

``` bash
    npm install -g bower
```

### Instalando o Grunt

``` bash
    npm install -g grunt-cli
```

### Instalando o Protractor

``` bash
    npm install -g protractor
```

### Instalando as dependências para a interface gráfica

``` bash
    cd huxley-app/
    bower install
```

### Instalando o Chrome Web Driver

Depois de instalar o Protractor pelo NPM.

``` bash
    cd huxley-app
    webdriver-manager --out_dir vendor/selenium update
```

### Realizando a build do projeto com o Grunt

``` bash
    cd huxley-app/
	npm install
    grunt
```

### Grunt no modo watch, para desenvolvimento

Quando o grunt watch estiver rodando, ele irá monitorar os arquivos do projeto e realizar builds conforme o necessário.

``` bash
    cd huxley-app/
    grunt watch
```

## Grunt

### Rodando os testes com o grunt

Testes e2e e unit:

``` bash
    grunt test
```

Testes e2e com Protractor:

``` bash
    grunt proractor
```


Testes de unidade com Karma:

``` bash
    grunt karma
```

## Intellij IDEA

### Instalando o plugin do Node.JS

1. Para instalar o plugin vá em File > Settings
2. Digite "Plugins" na busca do lado esquerdo da janela de settings e escolha plugins
3. Clique no botão Browse repositories... e digite "NodeJS".
4. Escolha NodeJS e clique no botão verde "Install plugin"

Reinicie o idea e pronto


### Rodando o Grunt do Intellij IDEA

1. No diretório huxley-app/ clique com o botão direito no arquivo Gruntfile.js
2. Escolha: Open Grunt Console
3. Escolha a Task ou Alias para rodar.

