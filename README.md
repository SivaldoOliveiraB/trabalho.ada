Para começar a executar o programa, depois de fazer as inserções no banco de dados,
conforme descrito nas intrunções do arquivo bancada.sql, no projeto meeting-room, 
será necessário executar o seguinte comando no banco de dados:

CREATE SEQUENCE conta_seq START 9;

Isso irá criar no banco de dados uma sequência chamada conta_seq, 
definindo o primeiro valor gerado será 9 (pois já existem oito contas cadastradas no bando de dados).
Esse SEQUECE conta_seq será usado para pegar o próximo número da conta a ser criado
