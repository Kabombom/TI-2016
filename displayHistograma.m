function displayHistograma(p,A)
    if(nargin==1)
       imhist( histogramaOcurrencias(p) );
    else
       histogram( histogramaOcurrencias(p,A) );
    end
end

