warning off

clear, clc, close all

% exercicio 1
imagem = imread('data/CT1.bmp');
som = [1 2; 3 4; 1 2; 3 4; 1 9];
num = [1 1 1 1 1 1 1 1 2 2 2 2 3 3 4 4];
str = 'aeiouaeioaeiaeakas';
alfStr = {'a' 'b' 'c' 'd' 'e' 'f' 'g' 'h' 'i' 'j' 'k' 'l' 'm' 'n' 'o' 'p' 'q' 'r' 's' 't' 'u' 'v' 'w' 'x' 'y' 'z'};
alfNumSom = {1 2 3 4 5 6 7 8 9 10};
% displayHistograma(num,alfNumSom);


% exercicio 2
% disp(sprintf('entropia: %d', entropia(num, alfNumSom)));

% exercicio 3
% distribuicaoEstatistica_Entropia('data/saxriff.wav');
% distribuicaoEstatistica_Entropia('data/Lena.bmp');
% distribuicaoEstatistica_Entropia('data/CT1.bmp');
% distribuicaoEstatistica_Entropia('data/Binaria.bmp');
% distribuicaoEstatistica_Entropia('data/Texto.txt');

% exercicio 4
% EntropiaHufflen();

% exercicio 6a)
query = [2 6 4 10 5 9 5 8 0 8];
target = [6 8 9 7 2 4 9 9 4 9 1 4 8 0 1 2 2 6 3 2 0 7 4 9 5 4 8 5 2 7 8 0 7 4 8 5 7 4 3 2 2 7 3 5 2 7 4 9 9 6];
alf = 0 : 10;
step = 1;
info = informacaoMutua(query, target, alf, step);
disp(info);

% exercicio 6b)
% [ som, freq, nBits ] = getSoundData('data/guitarSolo.wav');
[ somTarget01, freqTarget01, nBitsTarget01 ] = getSoundData('data/repeat.wav');
[ somTarget02, freqTarget02, nBitsTarget02 ] = getSoundData('data/repeatNoise.wav');

