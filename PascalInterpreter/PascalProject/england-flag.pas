var
	x,n:integer = 10;	
	j,i:integer;
	count:integer;
begin
	n:=11;
	for i:=0 to n do
	begin
		for j:=0 to n do
		begin
			if i=0 or i=n or j=0 or j=n then
				write('x');
			else if j=i or j=n-i then
				write('y');
			else if n%2=0 and ((n/2)=(j) or (n/2)=i) then
				write('z');
			else if n%2=1 and ((n/2=j-((j+1)%2)) or (n/2=i-((i+1)%2))) then
				write('a');
			else
				write(' ');		
		end
		writeln();
	end
end.
