warning off

clear, clc, close all

% exercicio 1
imagem = imread('CT1.bmp');
som = [1 2; 3 4; 1 2; 3 4; 1 9];
num = [1 1 1 1 1 1 1 1 2 2 2 2 3 3 4 4];
str = 'aeiouaeioaeiaeakas';
alfStr = {'a' 'b' 'c' 'd' 'e' 'f' 'g' 'h' 'i' 'j' 'k' 'l' 'm' 'n' 'o' 'p' 'q' 'r' 's' 't' 'u' 'v' 'w' 'x' 'y' 'z'};
alfNumSom = {1 2 3 4 5 6 7 8 9 10};
%graf = histogramaOcurrencias(num, alfNumSom);
%histogram(graf);

%exercicio 2
%entropia(num, alfNumSom);

%exercicio 3
distribuicaoEstatistica_Entropia_Audio('saxriff.wav');
distribuicaoEstatistica_Entropia_Imagem('Lena.bmp');
distribuicaoEstatistica_Entropia_Imagem('CT1.bmp');
distribuicaoEstatistica_Entropia_Imagem('Binaria.bmp');
