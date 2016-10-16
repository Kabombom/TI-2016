function ent = entropia(p, A) 
  
  if(nargin==1)
      frequencias = imhist(p);
      frequencias = transpose(frequencias);
  else
      graf = histogramaOcurrencias(p, A);
      %obter frequencias
      counts = histcounts(graf);
      frequencias = counts(1,:);
  end
  
  aux = size(p);
  %numero de elementos em p
  numElementos = aux(1) * aux(2);
  %calculo a probabilidade de cada elemento de ocorrer
  probs = arrayfun(@(x) x./numElementos, frequencias);
  %Uso a formula mas sem fazer somatorio
  preSum = arrayfun(@(x) x .* log2(1./x), probs);
  %Torno todos os elementos do tipo Nan em 0, se nao o fizer a sum vai dar Nan
  preSum(isnan(preSum)) = 0;
  %Somatorio para calcular a entropia
  ent = sum(preSum);
end
