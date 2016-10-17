function displayHistograma(p,A)

    % imagem
    if(nargin==1)
       imhist( histogramaOcurrencias(p) );
    % texto
    elseif(ischar(p))
       graf = histogramaOcurrencias(p,A);
       histogram(graf);
    % som
    else
        graf = histogramaOcurrencias(p,A);
        histogram(graf);
    end
    
end

