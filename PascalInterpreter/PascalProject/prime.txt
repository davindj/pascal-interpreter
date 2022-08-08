var
	i,j:integer=0;
	n,count:integer;
begin
	n:=1000;
	for i:=2 to n do
		begin
		count:=0;
		for j:=2 to i^0.5 do
			begin
			if i%j=0 then count:=1;
			if count>0 then 
				j:=i;
			if j<>2 then
				j:=j+1;
			end
		if count=0 then 
			writeln(i);
		if i<>2 then
			i:=i+1;
		end
end.
