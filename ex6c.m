% exercicio 6c
[ som, freq, nBits ] = getSoundData('data/guitarSolo.wav');
som = som(:);
som = som * 2^nBits;
d = 1/(2^nBits);
alf = num2cell(-1:d:1);
step = floor(1/4 * length(som));

for i=1:7
    file = sprintf('data/Song0%d.wav', i);
    [ target, freqTarget, nBitsTarget ] = getSoundData(file);
    target = target(:);
    target = target * 2^nBits;
    info = max(informacaoMutua(som, target, alf, step));
    fprintf(sprintf('Song0%d.wav: %f\n', i, info));
end