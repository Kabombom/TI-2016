function graf = histogramaOcurrencias(p, A)  

    % imagem
    if(nargin==1)
       	graf = p;
        
    % texto
    elseif(ischar(p))
        % converter letras para inteiros
        p = uint16(p);
        % converter alfabeto para inteiros
        convA = cellfun(@uint16, A); 
        % associa letras em A aos seu ASCCI code em convA
        graf = categorical(p,convA,A);
        
    % som
    else
        [lines, collumns] = size(p);
    
        % converte matriz numa linha apenas
        p = p(:);
        % converte celulas em numeros
        convA = cell2mat(A);
        counter = 1;
        maxConvA = length(convA);
        maxP = length(p);
        for i=1:maxConvA
 -            % fprintf('%f %% processedo - A filtar fonte\n', (i/maxConvA)*100);
 -            for j=1:maxP
 -                if(convA(i)==p(j))
 -                   graf(counter) = p(j);
 -                   counter = counter + 1;
 -                end
 -            end
 -        end
    end
    
end
