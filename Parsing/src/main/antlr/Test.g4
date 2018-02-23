grammar Test;

@header {
//    package parsers.Test;
}

main: 'Hello ' name '!';
name: ANY+;
ANY: .;