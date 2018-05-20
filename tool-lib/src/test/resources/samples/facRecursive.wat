(module
  (func $factorial (param i32) (result i32)
    get_local 0
    i32.const 0
    i32.eq
    if (result i32)
      i32.const 1
    else
      get_local 0
      get_local 0
      i32.const 1
      i32.sub
      call $factorial
      i32.mul
    end
  )
  (func $main (result i32)
    i32.const 5
    call $factorial
  )
  (export "factorial" (func $factorial))
  (export "main" (func $main))
)
