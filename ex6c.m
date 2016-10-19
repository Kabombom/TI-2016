% exercicio 6c
[ som, freq, nBits ] = getSoundData('data/saxriff.wav');
som = som(:);
som = som * 2^nBits;
d = 1/(2^nBits);
alf = num2cell(-1:d:1);
step = floor(1/4 * length(som));


array = zeros(7, 2);

for i=1:7
    file = sprintf('data/Song0%d.wav', i);
    [ target, freqTarget, nBitsTarget ] = getSoundData(file);
    target = target(:);
    target = target * 2^nBits;
    array(i, 1) = max(informacaoMutua(som, target, alf, step));
    array(i, 2) = i;
end

array_ordenado =  sortrows(array);

for i=1:7
    ind = array_ordenado(8-i,2);
    file = sprintf('Song0%d.wav', array_ordenado(8-i,2));
    fprintf('%s: %f\n', file, array(8-i,1));
end