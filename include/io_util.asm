section .data
  new_line_char:        equ  10
  default_buffer_size:  equ  64
  read_int_id:          equ  0
  ascii_digit_offset:	equ  48

section .bss
  char_buffer:          resb default_buffer_size

section .text
; reads input from standard input, byte by byte.
; expects a 0 in rdi if each byte of the input
;   should be correct by 48, the ascii offset.
read_chars:
  push rbp
  mov rbp, rsp
  sub rsp, 8

  mov [rbp - 8], rdi	     ; store rdi in free stack space. 
  mov r10, 0                 ; buffer size
  mov rsi, char_buffer       ; buffer write pos

; todo: maybe take file descriptor as parameter, 
; this would depend on whether, the language should
; support reading from files. (not a given)
next_char:
  mov rax, 0                 ; syscall id (read)
  mov rdi, 0                 ; file descriptor (stdin)
  mov rdx, 1                 ; 1 byte
  syscall

  inc rsi
  inc r10

  cmp byte [rsi - 1], new_line_char
  je end_of_input

  cmp byte [rbp - 8], read_int_id
  jne jump_to_next
  
  sub byte [rsi - 1], ascii_digit_offset

jump_to_next: 
  jmp next_char

end_of_input:
  mov rsp, rbp
  pop rbp

; returned values in rax and rcx.
  mov rax, char_buffer
  mov rcx, r10
  ret
