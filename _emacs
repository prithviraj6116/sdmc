(global-set-key "\C-x\C-b" 'buffer-menu)
(global-set-key "\C-\M-c" 'scroll-other-window-down)
(add-to-list 'load-path "~/.emacs.d")
(add-to-list 'load-path "~/.emacs.d/emacs-w3m-1.4.4")
(add-to-list 'load-path "~/.emacs.d/w3m-0.5.3/")


;; (setq browse-url-browser-function 'w3m-browse-url)
;;  (autoload 'w3m-browse-url "w3m" "Ask a WWW browser to show a URL." t)
;;  ;; optional keyboard short-cut
  (global-set-key "\C-xm" 'browse-url-at-point)
;; (setq w3m-use-cookies t)
