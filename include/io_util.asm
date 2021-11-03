global _start

section .data
  null_char:            equ 0
  new_line_char:        equ 10
  max_buffer_size:      equ 128
  default_buffer_size:  equ 128
  message:              db  "Hello world"

section .bss
  buffer:               resb  default_buffer_size

section .text
_start:
  mov r10, 0        ; buffer size
  mov rsi, buffer   ; buffer write pos

read_char_stdin:
  cmp r10, max_buffer_size
  je print_buffer

  mov rax, 0       ; syscall id (read)
  mov rdi, 0       ; file descriptor (stdin)
  mov rdx, 1       ; 1 byte
  syscall

  mov r9, 0
  mov r9b, byte [rsi]
  inc rsi
  inc r10

  cmp r9, 10
  jne read_char_stdin

  push buffer

print_buffer: ; this is just for testing.
  mov rax, 1       ; syscall id (read)
  mov rdi, 1       ; file descriptor (stdout)
  mov rsi, buffer  ;buffer  ; the buffer
  mov rdx, r10
  syscall

  ; terminate below
  mov rax, 60
  mov rdi, 0
  syscall
