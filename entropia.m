function ent = entropia(p, A) 
  
  if(nargin==1)
      frequencias = imhist(p);
      frequencias = transpose(frequencias);
  else
      graf = histogramaOcurrencias(p, A);
      % obter frequencias
      counts = histcounts(graf);
      frequencias = counts(1,:);
  end
  
  aux = size(p);
  % numero de elementos em p
  numElementos = aux(1) * aux(2);
  % probabilidade de cada elemento de ocorrer
  probs = arrayfun(@(x) x./numElementos, frequencias);
  % formula sem fazer somatorio
  preSum = arrayfun(@(x) x .* log2(1./x), probs);
  % tornar todos os elementos do tipo Nan em 0, caso contrario a soma daria Nan
  preSum(isnan(preSum)) = 0;
  % somatorio para calcular a entropia
  ent = sum(preSum);
  
end
