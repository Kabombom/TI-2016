warning off

clear, clc, close all

% exercicio 1
som = [1 2; 3 4; 1 2; 3 4; 1 9];
num = [1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 ];
str = 'aeiouaeioaeiaeakas';
alfStr = {'a' 'e' 'i' 'o' 'u'};
alfNumSom = {1 2 3 4 5 6 7 8 9 10};
histogramaOcurrencias(str, alfStr);

% exercicio 2
a = [1,2,3,1,2];
aAlf = [1,2,3];
entropia(a, aAlf);
