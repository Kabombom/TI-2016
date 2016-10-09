warning off

clear, clc, close all

% exercicio 1
imagem = imread('data/CT1.bmp');
som = [1 2; 3 4; 1 2; 3 4; 1 9];
num = [1 1 1 1 1 1 1 1 2 2 2 2 3 3 4 4];
str = 'aeiouaeioaeiaeakas';
alfStr = {'a' 'b' 'c' 'd' 'e' 'f' 'g' 'h' 'i' 'j' 'k' 'l' 'm' 'n' 'o' 'p' 'q' 'r' 's' 't' 'u' 'v' 'w' 'x' 'y' 'z'};
alfNumSom = {1 2 3 4 5 6 7 8 9 10};
alfImg = num2cell(0:255);
graf = histogramaOcurrencias(imagem);
%displayHistograma(graf);

%exercicio 2
entropia(num, alfNumSom);

%exercicio 3
%distribuicaoEstatistica_Entropia_Imagem('data/Lena.bmp');
%distribuicaoEstatistica_Entropia_Imagem('data/CT1.bmp');
%distribuicaoEstatistica_Entropia_Imagem('data/Binaria.bmp');
%distribuicaoEstatistica_Entropia_Audio('data/saxriff.wav');
