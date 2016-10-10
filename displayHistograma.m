function displayHistograma(p,A)
    if(nargin==1)
       imhist( histogramaOcurrencias(p) );
    else
       graf = histogramaOcurrencias(p,A);
       histogram(graf);
    end
end

