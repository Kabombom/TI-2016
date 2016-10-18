% exercicio 5
%Imagem
%obj = imread('data/Lena.bmp');
%alf = num2cell(0:255);

%Ficheiro de texto
% ficheiro = fopen('data/Texto.txt', 'r');
% obj = fscanf(ficheiro,'%c');
% fclose(ficheiro);       
% alf = {'a' 'b' 'c' 'd' 'e' 'f' 'g' 'h' 'i' 'j' 'k' 'l' 'm' 'n' 'o' 'p' 'q' 'r' 's' 't' 'u' 'v' 'w' 'x' 'y' 'z'};
% obj = uint16(obj);
% alf = cellfun(@uint16, alf);
% alf = num2cell(alf);

%Som - Nao recomendado, processo lento
% [obj, freq, nBits ] = getSoundData('data/saxriff.wav');
% d = 1/(2^nBits);
% alf = num2cell(-1:d:1);

%vetor de inteiros
obj = [2 4 1 0 2 0 1 6];
alf = num2cell(0:10);

alf = criarAlfabetoDePares(alf);
[lines, collumns] = size(obj);
obj = reshape(obj, [1, lines*collumns]);
obj = vec2mat(obj, 2);
obj = labelObj(alf,obj);
alf = labelAlf(alf);

displayHistograma(obj, alf);
set(gca, 'XTickLabel', []);
disp(sprintf('entropia: %f', entropia(obj, alf)));