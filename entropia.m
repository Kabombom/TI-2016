function ret = entropia(p, A)
  % exemplo: A= {S0 S1 S2 S3} com pk = probabilidade de Sk ocorrer em p
  % exemplo p0=0.5, p1=0.25, p2=0.125, p3=0.125
  % entropia = 0.5 * log2(1/0.5) + 0.25 * log2(1/0.25) + 0.125 * log2(1/0.125) + 0.125 * log2(1/0.125) = 1.75  
  
  if(nargin==1)
      frequencias = imhist(p);
      frequencias = transpose(frequencias);
  else
      %Obtenho o objecto do tipo categorical
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
  %Classico print
  s = sprintf('entropia %d\n', ent);
  disp(s);
end
