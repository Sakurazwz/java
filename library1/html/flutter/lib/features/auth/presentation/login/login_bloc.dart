import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:equatable/equatable.dart';
import '../../data/auth_repository.dart';
import '../../domain/user.dart';

// 登录事件基类
abstract class LoginEvent extends Equatable {
  @override
  List<Object?> get props => [];
}

// 登录提交事件
class LoginSubmitted extends LoginEvent {
  final String username;
  final String password;

  LoginSubmitted({required this.username, required this.password});

  @override
  List<Object?> get props => [username, password];
}

// 登录状态基类
abstract class LoginState extends Equatable {
  @override
  List<Object?> get props => [];
}

// 初始状态
class LoginInitial extends LoginState {}

// 加载中状态
class LoginLoading extends LoginState {}

// 登录成功状态
class LoginSuccess extends LoginState {
  final User user;

  LoginSuccess({required this.user});

  @override
  List<Object?> get props => [user];
}

// 登录失败状态
class LoginFailure extends LoginState {
  final String error;

  LoginFailure({required this.error});

  @override
  List<Object?> get props => [error];
}

// 登录 Bloc，处理登录业务逻辑
class LoginBloc extends Bloc<LoginEvent, LoginState> {
  final AuthRepository _authRepository = AuthRepository();

  LoginBloc() : super(LoginInitial()) {
    on<LoginSubmitted>(_onLoginSubmitted);
  }

  // 处理登录提交事件
  Future<void> _onLoginSubmitted(
    LoginSubmitted event,
    Emitter<LoginState> emit,
  ) async {
    emit(LoginLoading());
    try {
      final user = await _authRepository.login(event.username, event.password);
      emit(LoginSuccess(user: user));
    } catch (e) {
      emit(LoginFailure(error: e.toString()));
    }
  }
}
