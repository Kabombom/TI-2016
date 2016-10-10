function displayHistograma(p,A)
    %imagem
    if(nargin==1)
       imhist( histogramaOcurrencias(p) );
    %texto
    elseif(ischar(p))
       graf = histogramaOcurrencias(p,A);
       histogram(graf);
    %Som
    else
        %TO DO
    end
end

