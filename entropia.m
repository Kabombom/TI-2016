function ent = entropia(p, A)
  % exemplo: A= {S0 S1 S2 S3} com pk = probabilidade de Sk ocorrer em p
  % exemplo p0 = 0.5, p1=0.25, p2=0.125, p3=0.125
  % entropia =  0.5 * log2(1/0.5) + 0.25 * log2(1/0.25) + 0.125 * log2(1/0.125) + 0.125 * log2(1/0.125) = 1.75
  hist = zeros(1, length(A));
  for i = 1:length(A)
    hist(i) = length(find(p == i)) / length(A);
    end
  ent = -sum(hist.*log2(hist));
  s = sprintf('entropia %d\n', ent);
  disp(s);
end
