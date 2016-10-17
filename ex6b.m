% exercicio 6b)
[ som, freq, nBits ] = getSoundData('data/guitarSolo.wav');
[ somTarget01, freqTarget01, nBitsTarget01 ] = getSoundData('data/repeat.wav');
[ somTarget02, freqTarget02, nBitsTarget02 ] = getSoundData('data/repeatNoise.wav');

som = som(:);
somTarget01 = somTarget01(:);
som = som * 2^nBits;
somTarget01 = somTarget01 * 2^nBitsTarget01;
d = 1/(2^nBits);
alf = num2cell(-1:d:1);

step = floor(1/4 * length(som));
info = informacaoMutua(som, somTarget01, alf, step);
disp(info);
