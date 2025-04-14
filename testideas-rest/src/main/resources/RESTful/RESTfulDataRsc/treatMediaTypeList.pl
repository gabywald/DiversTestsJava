#!/usr/bin/perl -w

use strict;

my $inputFile	= "MediaTypeList.txt";
my $outputFile	= "MediaTypeList-export.txt";
my $current		= undef;
my %dataToGo	= (); 

open (INPUT, "<".$inputFile);
while (my $line = <INPUT>) {
	if ($line =~ /^\tstatic (String|MediaType)\s+([A-Z\_]+)$/) {
		print "\t[".$1."]\t{".$2."}\n";
		$current = $2."::".$1;
	} elsif ($line =~ /^\t(.*)$/) {
		print "\t--\n";
		if (defined $current) {
			$dataToGo{$current} = $1;
			$current = undef;
		} ## END ...
	} else { print "\t NOT {".$line."\n"; }
} ## END ...
close (INPUT);

print "\n\n\n";


open (OUTPUT, ">".$outputFile);
for my $element (sort(keys(%dataToGo))) {
	my $comment	= $dataToGo{$element};
	my @subELTs	= split("::", $element);
	print "\t[".$subELTs[1]."]\t{".$subELTs[0]."}\t".$comment."\n";
	
	my $type	= undef;
	my $content	= undef;
	if ($comment =~ /^A (String|MediaType) constant representing (".*?") media type.$/) 
		{ $type = $1;$content = $2; }
	if (! defined $content) { 
		print "\t WRONG comment ?";
		$type		= "UNK";
		$content	= "\"toCheck\"";
	} ## END ...
	
	if ($type eq "MediaType") 
		{ $content = "new MediaType(".$content.")"; }
	
	print OUTPUT "\t/** ".$comment." */\n";
	print OUTPUT "\tpublic static ".$subELTs[1]." ".$subELTs[0]." = ".$content.";\n";
} ## END ...
close (OUTPUT);