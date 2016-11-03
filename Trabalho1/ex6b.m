% exercicio 6b
[ som, freq, nBits ] = getSoundData('data/saxriff.wav');
[ somTarget01, freqTarget01, nBitsTarget01 ] = getSoundData('data/repeat.wav');
[ somTarget02, freqTarget02, nBitsTarget02 ] = getSoundData('data/repeatNoise.wav');

som = som(:);
somTarget01 = somTarget01(:);
somTarget02 = somTarget02(:);
som = som * 2^nBits;
somTarget01 = somTarget01 * 2^nBitsTarget01;
somTarget02 = somTarget02 * 2^nBitsTarget02;
d = 1/(2^nBits);
alf = num2cell(-1:d:1);
step = floor(1/4 * length(som));

info = informacaoMutua(som, somTarget01, alf, step);
disp(info);
plot(info);

info2 = informacaoMutua(som, somTarget02, alf, step);
disp(info2);
plot(info2);
