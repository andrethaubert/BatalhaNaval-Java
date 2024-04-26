package BatalhaNaval;

import java.util.Random;
import java.util.Scanner;

class BatalhaNaval2 {

    public static void main(String[] args) {
        Scanner ler = new Scanner(System.in);
        System.out.println("BEM VINDO A BATALHA NAVAL");

        // Declarando os tabuleiros para os jogadores e para a máquina
        char[][] tabuleiroJogador1 = new char[10][10];
        char[][] tabuleiroJogador2 = new char[10][10];
        char[][] tabuleiroMaquina = new char[10][10];
        char[][] tabuleiroAleatorio = new char[10][10];

        // Inicializando os tabuleiros
        inicializarTabuleiro(tabuleiroJogador1);
        inicializarTabuleiro(tabuleiroJogador2);
        inicializarTabuleiro(tabuleiroMaquina);
        inicializarTabuleiro(tabuleiroAleatorio);

        // Solicitando ao jogador que escolha o modo de jogo
        System.out.println("Você deseja jogar contra outro jogador (1) ou contra a máquina (2)?");
        int escolhaJogo = ler.nextInt();

        if (escolhaJogo == 1) {
            // Jogador vs Jogador
            System.out.println("Jogador 1:");
            System.out.println("Você deseja alocar manualmente (1) ou aleatoriamente?(2)");
            int alocarBarcos = ler.nextInt();
            if (alocarBarcos==1) {
                alocarBarcos(tabuleiroJogador1, ler);
            } else if (alocarBarcos==2) {
                alocarBarcosAleatoriamente(tabuleiroAleatorio);
            }

            // Mesmo procedimento para o jogador 2
            System.out.println("Jogador 2:");
            System.out.println("Você deseja alocar manualmente (1) ou aleatoriamente?(2)");
            int alocarBarcos2 = ler.nextInt();
            if (alocarBarcos2==1) {
                alocarBarcos(tabuleiroJogador2, ler);
            } else if (alocarBarcos==2) { // Aqui estava um erro, deveria ser alocarBarcos2==2
                alocarBarcosAleatoriamente(tabuleiroAleatorio);
                System.out.println("Modo de jogo indisponível, tente mais tarde por favor!");
            }
        } else if (escolhaJogo == 2) {
            // Jogador vs Máquina
            System.out.println("Você escolheu jogar contra a máquina!");

            // Jogador alocando barcos manualmente
            alocarBarcos(tabuleiroJogador1, ler);

            // Máquina alocando barcos aleatoriamente
            alocarBarcosAleatoriamente(tabuleiroJogador2);
        } else if (escolhaJogo == 3) {
            // Dois jogadores na mesma máquina
            System.out.println("Dois jogadores na mesma máquina!");

            // Jogador 1 alocando barcos manualmente
            System.out.println("Jogador 1:");
            alocarBarcos(tabuleiroJogador1, ler);

            // Jogador 2 com alocação aleatória
            alocarBarcosAleatoriamente(tabuleiroJogador2);
        }

        System.out.println("Vamos começar o jogo!");

        // Variáveis para controlar o turno do jogador e o tabuleiro que será atacado
        int jogadorAtual = 1;
        char[][] tabuleiroAtacado;
        char[][] tabuleiroAtaque; // Esta variável estava sendo usada mas não inicializada no caso de jogar contra a máquina

        // Determinando qual tabuleiro será atacado com base na escolha do modo de jogo
        if (escolhaJogo == 1 || escolhaJogo == 3) {
            tabuleiroAtacado = tabuleiroJogador2;
        } else {
            tabuleiroAtacado = tabuleiroJogador1;
            tabuleiroAtaque = tabuleiroJogador2;
        }

        // Loop principal do jogo
        while (!jogoAcabou(tabuleiroJogador1, tabuleiroJogador2)) {
            System.out.println("É a vez do Jogador " + jogadorAtual + ".");
            imprimirTabuleiro(tabuleiroAtacado);
            System.out.println("Informe a linha (0-9) e coluna (0-9) para ataque:");
            int linhaAtaque = ler.nextInt();
            int colunaAtaque = ler.nextInt();

            // Verificando se o ataque está dentro dos limites do tabuleiro
            if (linhaAtaque >= 0 && linhaAtaque < 10 && colunaAtaque >= 0 && colunaAtaque < 10) {
                if (tabuleiroAtacado[linhaAtaque][colunaAtaque] == '.') {
                    tabuleiroAtacado[linhaAtaque][colunaAtaque] = 'A'; // Marcando como água
                    System.out.println("Você atingiu a água.");
                } else if (tabuleiroAtacado[linhaAtaque][colunaAtaque] == 'N') {
                    realizarAtaque(tabuleiroAtacado, linhaAtaque, colunaAtaque);
                    System.out.println("Você acertou um navio!");
                } else {
                    System.out.println("Você já atacou essa posição. Escolha outra.");
                    continue; // Pula para a próxima iteração do loop
                }
            } else {
                System.out.println("Posição inválida. Informe valores entre 0 e 9.");
                continue; // Pula para a próxima iteração do loop
            }

            // Trocando o turno do jogador
            if (jogadorAtual == 1) {
                jogadorAtual = 2;
                tabuleiroAtacado = tabuleiroJogador1;
                tabuleiroAtaque = tabuleiroJogador2;
            } else {
                jogadorAtual = 1;
            }
        }

        // Verificando o resultado do jogo e imprimindo mensagem correspondente
        if (venceu(tabuleiroJogador1) || venceu(tabuleiroJogador2)) {
            System.out.println("Parabéns! O Jogador " + (venceu(tabuleiroJogador1) ? "1" : "2") + " venceu!");
        } else {
            System.out.println("Empate! Ambos os jogadores perderam.");
        }
    }

    // Método para inicializar o tabuleiro com pontos representando o oceano
    public static void inicializarTabuleiro(char[][] tabuleiro) {
        for (int i = 0; i < tabuleiro.length; i++) {
            for (int j = 0; j < tabuleiro[i].length; j++) {
                tabuleiro[i][j] = '.';
            }
        }
    }

    // Método para alocar barcos no tabuleiro manualmente pelo jogador
    public static void alocarBarcos(char[][] tabuleiro, Scanner ler) {
        // Definição dos tipos e quantidades de navios
        int[][] navios = {
                {4, 1}, {3, 2}, {3, 3}, {2, 4}, {2, 5}, {2, 6}, {1, 7}, {1, 8}, {1, 9}, {1, 10}
        };

        for (int[] navio : navios) {
            boolean alocado = false;
            while (!alocado) {
                System.out.println("Alocação do barco (ocupando " + navio[0] + " espaços):");
                System.out.println("Informe a linha e a coluna inicial:");
                int linha = ler.nextInt();
                int coluna = ler.nextInt();
                System.out.println("O barco será vertical (V) ou horizontal (H)?");
                char direcao = ler.next().toUpperCase().charAt(0);
                boolean valido = true;

                // Verificando se a posição de alocação é válida e não ocupada
                for (int i = 0; i < navio[0]; i++) {
                    if (direcao == 'V') {
                        if (linha + i >= 10 || tabuleiro[linha + i][coluna] != '.') {
                            valido = false;
                            break;
                        }
                    } else {
                        if (coluna + i >= 10 || tabuleiro[linha][coluna + i] != '.') {
                            valido = false;
                            break;
                        }
                    }
                }

                // Alocando o barco se a posição for válida
                if (valido) {
                    for (int i = 0; i < navio[0]; i++) {
                        if (direcao == 'V') {
                            tabuleiro[linha + i][coluna] = 'N';
                        } else {
                            tabuleiro[linha][coluna + i] = 'N';
                        }
                    }
                    alocado = true;
                    imprimirTabuleiro(tabuleiro);
                } else {
                    System.out.println("Posição inválida ou ocupada. Informe outra posição.");
                }
            }
        }
    }

    // Método para alocar barcos aleatoriamente no tabuleiro
    public static void alocarBarcosAleatoriamente(char[][] tabuleiro) {
        // Definição dos tipos e quantidades de navios
        int[][] navios = {
                {4, 1}, {3, 2}, {3, 3}, {2, 4}, {2, 5}, {2, 6}, {1, 7}, {1, 8}, {1, 9}, {1, 10}
        };

        Random rand = new Random();

        for (int[] navio : navios) {
            boolean alocado = false;
            while (!alocado) {
                int linha = rand.nextInt(10);
                int coluna = rand.nextInt(10);
                char direcao = rand.nextBoolean() ? 'V' : 'H';
                boolean valido = true;

                // Verificando se a posição de alocação é válida e não ocupada
                for (int i = 0; i < navio[0]; i++) {
                    if (direcao == 'V') {
                        if (linha + i >= 10 || tabuleiro[linha + i][coluna] != '.') {
                            valido = false;
                            break;
                        }
                    } else {
                        if (coluna + i >= 10 || tabuleiro[linha][coluna + i] != '.') {
                            valido = false;
                            break;
                        }
                    }
                }

                // Alocando o barco se a posição for válida
                if (valido) {
                    for (int i = 0; i < navio[0]; i++) {
                        if (direcao == 'V') {
                            tabuleiro[linha + i][coluna] = 'N';
                        } else {
                            tabuleiro[linha][coluna + i] = 'N';
                        }
                    }
                    alocado = true;
                }
            }
        }
    }

    // Método para realizar um ataque na posição informada
    public static void realizarAtaque(char[][] tabuleiro, int linha, int coluna) {
        tabuleiro[linha][coluna] = 'X'; // Marcando como atingido
    }

    // Método para verificar se o jogo acabou
    public static boolean jogoAcabou(char[][] tabuleiroJogador1, char[][] tabuleiroJogador2) {
        return venceu(tabuleiroJogador1) || venceu(tabuleiroJogador2);
    }

    // Método para verificar se todos os navios foram afundados
    public static boolean venceu(char[][] tabuleiroJogador) {
        for (int i = 0; i < tabuleiroJogador.length; i++) {
            for (int j = 0; j < tabuleiroJogador[i].length; j++) {
                if (tabuleiroJogador[i][j] == 'N') {
                    return false; // Ainda existem navios no tabuleiro do jogador
                }
            }
        }
        return true; // Todos os navios do jogador foram afundados
    }

    // Método para imprimir o tabuleiro com representação visual
    public static void imprimirTabuleiro(char[][] tabuleiro) {
        System.out.print("  ");
        for (int i = 0; i < tabuleiro.length; i++) {
            System.out.print(i + " ");
        }
        System.out.println();

        for (int i = 0; i < tabuleiro.length; i++) {
            System.out.print(i + " ");
            for (int j = 0; j < tabuleiro[i].length; j++) {
                System.out.print(tabuleiro[i][j] + " ");
            }
            System.out.println();
        }
    }
}
