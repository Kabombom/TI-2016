function distribuicaoEstatistica_Entropia_Audio(file)
    [y, fs] = audioread(file);
    info = audioinfo(file);
    nbits = info.BitsPerSample;
    p = audioplayer(y, fs, nbits);
    alfabeto = linspace(-1, 1, 2000); % returns a row vector of 2000 evenly spaced points between -1 and 1.
    %graf = histogramaOcurrencias(matrizUnidimensional, alfabeto);
    %histogram(graf);
    %entropia(y, alfabeto);
end
