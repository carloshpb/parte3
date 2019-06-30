package server_client.constants;

public enum StringsConstants {

    MENU_OPTIONS ("1 - Criar mensagem (Create)\n2 - Ler mensagem (Read)\n3 - Atualizar mensagem (Update)\n4 - Deletar mensagem (Delete)\n5 - Sair (Quit)\nEscolha a opção: "),
    MESSAGE_CREATION_SUCCESS ("Mensagem criada com sucesso!"),
    MESSAGE_CREATION_SUCCESS_ID ("Mensagem criada com sucesso! Seu novo ID é : "),
    MESSAGE_DELETE_SUCCESS ("Mensagem deletada com sucesso!"),
    MESSAGE_UPDATE_SUCCESS ("Mensagem atualizada com sucesso!"),
    TITLE ("~~ Gerenciador de Mensagens ~~\n\n-------------------------------"),
    TYPE_MESSAGE("Digite a mensagem : "),
    TYPE_ID("Digite o ID da mensagem : "),
    WAITING_CLIENT ("Servidor iniciado. A espera de um cliente conectar..."),


    ERR_CHOOSE_SINGLE_OPTION ("Escolha apenas uma das opções!"),
    ERR_EMPTY_SAVE_MESSAGE ("Não foi possível salvar a mensagem, pois ela está vazia."),
    ERR_EMPTY_UPDATE_MESSAGE ("Não foi possível atualizar a mensagem, pois a atualização está vazia."),
    ERR_EXISTENT_MESSAGE ("Esta mensagem já existe no banco de dados."),
    ERR_INVALID_OPTION ("Opção inválida."),
    ERR_NO_ANSWER("Não obteve resposta do servidor. Desconectando ..."),
    ERR_NO_ARGS("Echo server requires 1 argument: <Listen Port>"),
    ERR_NON_EXISTENT_ID ("Este ID não existe."),
    ERR_NON_INT ("Valor digitado não é inteiro. Digite um ID que seja número inteiro.");

    private final String text;

    StringsConstants(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return this.text;
    }
}
