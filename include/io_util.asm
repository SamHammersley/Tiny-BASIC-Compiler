section .data
  new_line_char:        equ  10
  default_buffer_size:  equ  64
  read_int_id:          equ  0

section .bss
  char_buffer:          resb default_buffer_size

section .text

; expects a 0 in rdi if each byte of the input correct by 48, the ascii offset.
read_chars:
  pop r8
  push rdi
  mov r10, 0                 ; buffer size
  mov rsi, char_buffer       ; buffer write pos

next_char:
  mov rax, 0                 ; syscall id (read)
  mov rdi, 0                 ; file descriptor (stdin)
  mov rdx, 1                 ; 1 byte
  syscall

  mov r9, 0
  mov r9b, byte [rsi]
  inc rsi
  inc r10

  cmp r9, new_line_char
  je end_of_input

  pop rdi
  cmp r11, read_int_id
  jne jump_to_next
  
  add byte [rsi-1], 1

jump_to_next: 
  jmp next_char

end_of_input:
  push char_buffer
  push r10
  push r8
  ret
