function agrupamentoSimbolos(filename)

    [path,name,ext] = fileparts(filename);
    
    % imagem
    if(strcmp(ext,'.bmp'))
        disp('A processar imagem');
        obj = imread(filename);
        alf = num2cell(0:255);
    
    % som
    elseif(strcmp(ext,'.wav'))
        disp('A processar som');
        [obj, freq, nBits ] = getSoundData(filename);
        d = 1/(2^nBits);
        alf = num2cell(-1:d:1);
        
    % texto
    else
        disp('A processar ficheiro de texto');
        ficheiro = fopen(filename, 'r');
        obj = fscanf(ficheiro,'%c');
        fclose(ficheiro);       
        alf = {'a' 'b' 'c' 'd' 'e' 'f' 'g' 'h' 'i' 'j' 'k' 'l' 'm' 'n' 'o' 'p' 'q' 'r' 's' 't' 'u' 'v' 'w' 'x' 'y' 'z'};
        obj = uint16(obj);
        alf = cellfun(@uint16, alf);
        alf = num2cell(alf);
    end
    
    alf = criarAlfabetoDePares(alf);
    [lines, collumns] = size(obj);
    obj = reshape(obj, [1, lines*collumns]);
    obj = vec2mat(obj, 2);
    obj = labelObj(alf,obj);
    alf = labelAlf(alf);
    set(gca, 'XTickLabel', []);  % retira os valores do label x
    displayHistograma(obj,alf);
    fprintf('entropia de %s: %f\n', name, entropia(obj, alf));

end

