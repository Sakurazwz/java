import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:equatable/equatable.dart';
import '../../data/auth_repository.dart';

// 注册事件基类
abstract class RegisterEvent extends Equatable {
  @override
  List<Object?> get props => [];
}

// 注册提交事件
class RegisterSubmitted extends RegisterEvent {
  final String username;
  final String password;

  RegisterSubmitted({required this.username, required this.password});

  @override
  List<Object?> get props => [username, password];
}

// 注册状态基类
abstract class RegisterState extends Equatable {
  @override
  List<Object?> get props => [];
}

// 初始状态
class RegisterInitial extends RegisterState {}

// 加载中状态
class RegisterLoading extends RegisterState {}

// 注册成功状态
class RegisterSuccess extends RegisterState {}

// 注册失败状态
class RegisterFailure extends RegisterState {
  final String error;

  RegisterFailure({required this.error});

  @override
  List<Object?> get props => [error];
}

// 注册 Bloc，处理注册业务逻辑
class RegisterBloc extends Bloc<RegisterEvent, RegisterState> {
  final AuthRepository _authRepository = AuthRepository();

  RegisterBloc() : super(RegisterInitial()) {
    on<RegisterSubmitted>(_onRegisterSubmitted);
  }

  // 处理注册提交事件
  Future<void> _onRegisterSubmitted(
    RegisterSubmitted event,
    Emitter<RegisterState> emit,
  ) async {
    emit(RegisterLoading());
    try {
      await _authRepository.register(event.username, event.password);
      emit(RegisterSuccess());
    } catch (e) {
      emit(RegisterFailure(error: e.toString()));
    }
  }
}
