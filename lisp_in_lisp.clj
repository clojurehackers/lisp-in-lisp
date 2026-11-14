;; Este arquivo preserva os nomes e a progressão conceitual do material original.
;; As implementações dos exercícios estão em branco; as primitivas de adaptação
;; abaixo já estão definidas para que o código possa continuar usando car, cdr,
;; cons, atom? e eqv? como no Lisp original.
;;
;; Uso no REPL:
;;   clj
;;   (load-file "lisp_in_lisp_exercises.clj")
;;
;; Implemente cada função e avalie sua definição no REPL.

(ns lisp-in-lisp
  ;; Clojure já possui cons e atom?, mas com a semântica de Clojure.
  ;; Excluímos esses nomes para definir aqui as versões usadas pelo mini-Lisp.
  (:refer-clojure :exclude [atom? cons]))

;; -----------------------------------------------------------------------------
;; Vocabulário do Lisp original: adaptação das primitivas para Clojure
;; -----------------------------------------------------------------------------
;; Mantemos os nomes do original porque eles tornam a leitura dos algoritmos
;; muito mais próxima do texto de referência. Estas funções são uma pequena
;; camada de compatibilidade: por baixo, usam as sequências do Clojure.
;;
;; car: em Scheme, devolve o primeiro elemento de um par/lista. Em Clojure,
;;      first faz isso para uma sequência. O nome car é mantido para que os
;;      algoritmos possam ser lidos quase literalmente.
(defn car [l]
  (clojure.core/first l))

;; cdr: em Scheme, devolve a cauda da lista. Em Clojure, rest devolve a sequência
;;      sem o primeiro elemento. Em particular, a cauda de uma lista unitária
;;      é (), o que combina com a representação de lista vazia usada aqui.
(defn cdr [l]
  (clojure.core/rest l))

;; cons: Clojure já possui cons com esse nome e comportamento adequado para
;;       construir sequências. O wrapper apenas mantém uma camada uniforme e
;;       deixa explícito que o mini-Lisp usa essa operação como sua primitiva.
;;       Diferentemente de Scheme, a cauda precisa ser uma sequência (ou nil):
;;       não há representação nativa de pares pontuados como (a . b).
(defn cons [x y]
  (clojure.core/cons x y))

;; pair?: não há um pair? equivalente no Clojure. Nesta adaptação, uma lista
;;        não vazia (ou outra seq Clojure) representa um par/lista não vazia.
(defn pair? [x]
  (boolean (and (seq? x) (clojure.core/seq x))))

;; atom?: no Scheme do material, atom? é falso para listas não vazias e
;;        verdadeiro nos demais casos. Clojure também tem atom?, mas ela testa
;;        se o valor é uma referência Atom criada com atom. Por isso, neste
;;        namespace, definimos atom? com o sentido do Lisp original.
(defn atom? [x]
  (clojure.core/not (pair? x)))

;; eqv?: usamos = como aproximação para os exemplos deste mini-Lisp. A igualdade
;;        de Clojure é estrutural; não é idêntica em todos os casos à semântica
;;        de eqv? de Scheme, especialmente para objetos compostos.
(defn eqv? [x y]
  (clojure.core/= x y))

;; Seletores compostos usados no material original. Scheme costuma oferecer
;; combinações como caar/cadar diretamente; aqui elas são definidas a partir de
;; car e cdr para manter a notação original.
(defn caar [l] (car (car l)))
(defn cadr [l] (car (cdr l)))
(defn cdar [l] (cdr (car l)))
(defn cadar [l] (car (cdr (car l))))
(defn caddr [l] (car (cdr (cdr l))))

;; Observações importantes sobre a representação:
;; - usamos listas Clojure, escritas como '(a b c), para representar listas Lisp;
;; - a lista vazia é '(), e não nil;
;; - car/cdr pressupõem uma lista não vazia, como no material original;
;; - a operação cons constrói listas próprias; pares pontuados não são
;;   representados nesta versão;
;; - no mini-Lisp, somente false é falso. nil e '() são tratados como verdadeiros
;;   quando aparecem como resultados de predicados, como em Scheme.

;; -----------------------------------------------------------------------------
;; Os sete exercícios
;; -----------------------------------------------------------------------------

;; Questão 1
;; Defina my-nil? que recebe um argumento e retorna true se ele for a lista
;; vazia '() e false caso contrário.
(defn my-nil? [x]
  ;; Implemente aqui usando eqv?.
  nil)

;; Questão 2
;; Defina my-and que recebe dois argumentos e retorna true se ambos forem
;; verdadeiros; caso contrário, retorna false.
;; Para acompanhar Scheme, somente false deve ser considerado falso.
(defn my-and [x y]
  nil)

;; Questão 3
;; Defina my-not que recebe um argumento e retorna false se ele for verdadeiro
;; e true caso contrário. Somente false deve ser considerado falso.
(defn my-not [x]
  ;; Implemente aqui.
  nil)

;; Questão 4
;; Defina my-append que recebe duas listas, x e y, e retorna uma nova sequência
;; formada pelos elementos de x seguidos pelos elementos de y. Se x for '(),
;; retorna y.
;; Observação: o exemplo Scheme (my-append '(1 2) 3) forma um par pontuado.
;; Esta adaptação usa sequências Clojure e, portanto, espera uma cauda sequenciável.
(defn my-append [x y]
  ;; Implemente aqui usando car, cdr e cons.
  nil)

;; Questão 5
;; Defina my-search que recebe x (um valor qualquer) e l (uma lista), retornando
;; true se x estiver em l e false caso contrário.
(defn my-search [x l]
  ;; Implemente aqui usando eqv?.
  nil)

;; Questão 6
;; Defina my-pairs que recebe duas listas x e y e retorna uma lista de pares.
;; Cada par reúne os elementos que ocupam a mesma posição nas listas originais.
;; A lista resultante termina quando uma das listas terminar.
;; Exemplo: (my-pairs '(1 2 3) '(a b c)) => ((1 a) (2 b) (3 c))
(defn my-pairs [x y]
  ;; Implemente aqui usando my-nil?, pair?, my-and, car, cdr e cons.
  nil)

;; Questão 7
;; Defina my-assoc que recebe x e uma alist y, isto é, uma lista de pares
;; ((k1 v1) (k2 v2) ...). Procure o par cuja primeira posição seja igual a x e
;; retorne seu segundo elemento. Considere que x será encontrado.
(defn my-assoc [x y]
  ;; Implemente aqui usando caar, cadar, cdr e eqv?.
  nil)

;; -----------------------------------------------------------------------------
;; Construção do interpretador
;; -----------------------------------------------------------------------------

;; Começaremos definindo my-apply com um contexto vazio. Depois, acrescentaremos
;; o argumento a, uma alist que guarda as associações disponíveis. A versão
;; final abaixo usa três argumentos, como na parte final do material original.

(declare my-apply my-eval my-evcon my-evlis)

;; my-apply recebe:
;;   fn: uma operação primitiva ou uma expressão que representa uma função;
;;   l:  a lista de argumentos;
;;   a:  a alist com o contexto atual.
;; Retorna o valor da aplicação de fn aos elementos de l no contexto a.
(defn my-apply [fn l a]
  ;; Implemente aqui os casos car, cdr, cons, atom?, eqv?, label e lambda.
  nil)

;; my-eval recebe uma sexp e uma alist, e retorna o valor da expressão no
;; contexto informado. Símbolos são procurados na alist; números e booleanos
;; Clojure são valores literais e permanecem como estão.
(defn my-eval [e a]
  ;; Implemente aqui os casos de símbolos, quote, cond e aplicações.
  nil)

;; my-evcon percorre as cláusulas de cond em ordem. Avalia cada predicado e,
;; assim que o resultado for diferente de false, avalia a expressão associada.
(defn my-evcon [c a]
  ;; Implemente aqui usando my-eval, caar, cadar e cdr.
  nil)

;; my-evlis percorre uma lista de sexps e avalia cada elemento no contexto a,
;; preservando a ordem dos elementos.
(defn my-evlis [m a]
  ;; Implemente aqui usando my-nil?, cons, car, cdr e my-eval.
  nil)

;; A surpresa: quando as funções anteriores estiverem implementadas,
;; my-evalquote aplica uma função do mini-Lisp sem contexto inicial.
(defn my-evalquote [fn l]
  (my-apply fn l '()))

;; Exemplos do material original, adaptados para os booleanos Clojure.
;; Remova o comentário de cada expressão no REPL para avaliá-la depois de
;; implementar as funções.
(comment
  (my-evalquote 'eqv? '(a a))
  (my-evalquote 'cons '(a (b c)))

  (my-evalquote '(lambda (x y)
                         (cond ((eqv? x y) true)
                               (true false)))
                '(1 2))

  (my-evalquote '(label search
                        (lambda (x y)
                                (cond ((eqv? y '()) false)
                                      ((eqv? x (car y)) true)
                                      (true (search x (cdr y))))))
                '(5 (1 2 3 4 5)))

  (my-evalquote '(label search
                        (lambda (x y)
                                (cond ((eqv? y '()) false)
                                      ((eqv? x (car y)) true)
                                      (true (search x (cdr y))))))
                '(6 (1 2 3 4 5)))

  ;; Manipulação do ambiente (alist).
  (my-eval 'x '((x 2) (y b)))

  (my-eval '(cond ((atom? x) 'atom)
                  (not-found 'pair))
           '((x 2) (y (1 2)) (not-found true)))

  (my-eval '(f '(b c))
           '((f (lambda (x) (cons 'a x)))))

  (my-eval '((lambda (x) (cons 'a x)) '(b c))
           '((f (lambda (x) (cons 'a x)))))

  (my-eval '((label firstatom
                    (lambda (x)
                            (cond ((atom? x) x)
                                  (true (firstatom (car x))))))
             y)
           '((y ((a b) (c d))))))
