function distribuicaoEstatistica_Entropia_Audio(file)
    [y, fs] = audioread(file);
    info = audioinfo(file);
    nbits = info.BitsPerSample;
    p = audioplayer(y, fs, nbits);
    alfabeto = linspace(-1, 1, 2000); % returns a row vector of 2000 evenly spaced points between -1 and 1.
    alfabeto = mat2cell(alfabeto,1,ones(1,size(alfabeto,2)));
    graf = histogramaOcurrencias(y, alfabeto);
    histogram(graf);
    entropia(y, alfabeto);
end
