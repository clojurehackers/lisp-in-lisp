;; The Roots of Lisp — soluções em Clojure
;;
;; Este arquivo acompanha lisp_in_lisp_exercises.clj. Ele mantém os nomes e a
;; estrutura do material original, acrescentando apenas a camada necessária
;; para que as primitivas Lisp funcionem sobre listas/seqs do Clojure.
;;
;; Uso no REPL:
;;   (load-file "lisp_in_lisp_solutions.clj")
;;
;; As funções ficam no namespace roots-of-lisp.solutions.

(ns lisp-in-lisp-solutions
  (:refer-clojure :exclude [atom? cons]))

;; -----------------------------------------------------------------------------
;; Sete primitivas do Lisp original sobre sequências Clojure
;; -----------------------------------------------------------------------------
;; Mantemos car/cdr para que os algoritmos continuem parecidos com o original.
;; car equivale a first; cdr equivale a rest. Essa camada também documenta
;; precisamente onde a representação do Clojure difere de pares Scheme.

(defn car [l]
  (clojure.core/first l))

(defn cdr [l]
  (clojure.core/rest l))

(defn cons [x y]
  (clojure.core/cons x y))

(defn pair? [x]
  (boolean (and (seq? x) (clojure.core/seq x))))

(defn atom? [x]
  ;; No material original, atom? é o complemento de pair?.
  ;; A atom? nativa do Clojure testa referências Atom, por isso este namespace
  ;; exclui o nome nativo e define a versão com a semântica de Lisp.
  (clojure.core/not (pair? x)))

(defn eqv? [x y]
  ;; Aproximação para este subconjunto: = faz igualdade estrutural em Clojure.
  ;; É suficiente para os símbolos, números e listas vazias usados nos exemplos,
  ;; mas não reproduz todos os detalhes de eqv? de Scheme para objetos compostos.
  (clojure.core/= x y))

;; Seletores compostos presentes no código Scheme original.
(defn caar [l] (car (car l)))
(defn cadr [l] (car (cdr l)))
(defn cdar [l] (cdr (car l)))
(defn cadar [l] (car (cdr (car l))))
(defn caddr [l] (car (cdr (cdr l))))

;; Definiremos a seguir sete primitivas, que deverão ser as únicas ferramentas
;; utilizadas para a solução dos exercícios a seguir, além dos próprios exercícios.

;; 1. (quote x) ou 'x, returna x:
(quote x)

;; 2, (atom? x) retorna o átomo `true` se o valor de `x` for um átomo ou a lista
;; vazia.
(atom? 'x)
(atom? '(a b c))
(atom? '())

;; 3. (eqv? x y) retorna `true` se os valores de `x` e `y` forem o mesmo átomo ou
;; ambos são a lista vazia.
(eqv? 'a 'a)
(eqv? 'a 'b)
(eqv? '() '())

;; 4. (car x) espera que o valor de x seja uma lista e retorna o primeiro elemento
(car '(a b c))

;; 5. (cdr x) espera que o valor de x seja uma lista retorna tudo, exceto o primeiro
;; elemento
(cdr '(a b c))

;; 6. (cons x y) espera que o valor de `y` seja um lista e retorna a lista contendo
;; o alor de `x` seguido dos elementos de `y`
(cons 'a '(b c))
(cons 'a (cons 'b (cons 'c '())))
(car (cons 'a '(b c)))
(cdr (cons 'a '(b c)))

;; 7. (cond p1 e1 ... pn en) é computado da seguinte maneira:
;; A expressão `p` é computada em ordem até alguma retornar `true`.
;; Quando uma é encontrada, o valor da expressão `e` a seguir é
;; retornada como o valor de toda a expressão `cond`.
(cond (eqv? 'a 'b) 'first
      (atom? 'a)   'second)

;; Além destas primitivas também
;; -----------------------------------------------------------------------------
;; Os seis exercícios
;; -----------------------------------------------------------------------------

(defn my-nil? [x]
  (eqv? x '()))

(defn my-and [x y]
  ;; Scheme considera apenas #f falso. O if do Clojure também considera nil
  ;; falso; por isso testamos explicitamente false? em vez de usar x e y como
  ;; condições diretamente.
  (cond x
        (cond y    true
              true false)
        true false))

(defn my-not [x]
  (cond x    false
        true true))

(defn my-append [x y]
  ;; A definição é a mesma recursão do material original. Nesta adaptação, y
  ;; deve ser uma sequência Clojure; pares pontuados como (1 2 . 3) não são
  ;; representados pelas listas nativas do Clojure.
  (cond (my-nil? x) y
        true        (cons (car x)
                          (my-append (cdr x) y))))

(defn my-pairs [x y]
  (cond
    (my-and (my-nil? x) (my-nil? y)) '()

    (my-and (my-not (atom? x)) (my-not (atom? y)))
    (cons (list (car x) (car y))
          (my-pairs (cdr x) (cdr y)))))

(defn my-assoc [x y]
  (cond (eqv? (caar y) x) (cadar y)
        true              (my-assoc x (cdr y))))

;; -----------------------------------------------------------------------------
;; my-apply, my-eval, my-evcon e my-evlis
;; -----------------------------------------------------------------------------

(declare my-apply my-eval my-evcon my-evlis)

(defn my-apply [fn l a]
  (cond (atom? fn)
        (cond (eqv? fn 'car)   (caar l)
              (eqv? fn 'cdr)   (cdar l)
              (eqv? fn 'cons)  (cons (car l) (cadr l))
              (eqv? fn 'atom?) (atom? (car l))
              (eqv? fn 'eqv?)  (eqv? (car l) (car l))
              true             (my-apply (my-eval fn a) l a))

        (eqv? (car fn) 'label)
        (my-apply (caddr fn) l (cons (list (cadr fn) (caddr fn)) a))

        (eqv? (car fn) 'lambda)
        (my-eval (caddr fn) (my-append (my-pairs (cadr fn) l) a))))

(defn my-eval [e a]
  (cond (atom? e) (my-assoc e a)
        (atom? (car e))
        (cond (eqv? (car e) 'quote) (cadr e)
              (eqv? (car e) 'cond)  (my-evcon (cdr e) a)
              true                  (my-apply (car e) (my-evlis (cdr e) a) a))
        true (my-apply (car e) (my-evlis (cdr e) a) a)))

(defn my-evcon [c a]
  (cond (my-eval (caar c) a)
        (my-eval (cadar c) a)

        true (my-evcon (cdr c) a)))

(defn my-evlis [m a]
  (cond (my-nil? m) '()
        true        (cons (my-eval (car m) a)
                          (my-evlis (cdr m) a))))

;; A expressão de uma linha que dá acesso ao interpretador sem ambiente inicial.
(defn my-evalquote [fn l]
  (my-apply fn l '()))

;; -----------------------------------------------------------------------------
;; Exemplos do material original
;; -----------------------------------------------------------------------------
  ;; Primitivas
(my-evalquote 'eqv? '(a a))
(my-evalquote 'cons '(a (b c)))

  ;; lambda e cond
(my-evalquote '(lambda (x y)
                       (cond ((eqv? x y) true)
                             (true false)))
              '(1 2))

  ;; label permite definir uma função recursiva no próprio mini-Lisp.
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
         '((y ((a b) (c d)))))
