function out = criarAlfabetoDePares(alf)
    alfVec = cell2mat(alf);
    [x,y] = meshgrid(alfVec, alfVec);
    [lines, collumns] = size(x);
    x = reshape(x, [lines*collumns, 1]);
    [lines, collumns] = size(y);
    y = reshape(y, [lines*collumns, 1]);
    out = [x y];
    out = num2cell(out);
end