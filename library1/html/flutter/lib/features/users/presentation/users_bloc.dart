import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:equatable/equatable.dart';
import '../data/user_repository.dart';
import '../../auth/domain/user.dart';

// 用户管理事件
abstract class UsersEvent extends Equatable {
  @override
  List<Object?> get props => [];
}

// 加载用户列表事件
class UsersLoadRequested extends UsersEvent {}

// 搜索用户事件
class UsersSearchRequested extends UsersEvent {
  final String query;

  UsersSearchRequested({required this.query});

  @override
  List<Object?> get props => [query];
}

// 删除用户事件
class UsersDeleteRequested extends UsersEvent {
  final int userId;

  UsersDeleteRequested({required this.userId});

  @override
  List<Object?> get props => [userId];
}

// 更新用户角色事件
class UsersRoleUpdateRequested extends UsersEvent {
  final int userId;
  final String role;

  UsersRoleUpdateRequested({required this.userId, required this.role});

  @override
  List<Object?> get props => [userId, role];
}

// 用户管理状态
abstract class UsersState extends Equatable {
  @override
  List<Object?> get props => [];
}

// 初始状态
class UsersInitial extends UsersState {}

// 加载中状态
class UsersLoading extends UsersState {}

// 加载完成状态
class UsersLoaded extends UsersState {
  final List<User> users;

  UsersLoaded({required this.users});

  @override
  List<Object?> get props => [users];
}

// 操作成功状态
class UsersSuccess extends UsersState {
  final String message;

  UsersSuccess({required this.message});

  @override
  List<Object?> get props => [message];
}

// 错误状态
class UsersError extends UsersState {
  final String message;

  UsersError({required this.message});

  @override
  List<Object?> get props => [message];
}

// 用户管理 Bloc，处理用户相关业务逻辑
class UsersBloc extends Bloc<UsersEvent, UsersState> {
  final UserRepository _userRepository = UserRepository();

  UsersBloc() : super(UsersInitial()) {
    on<UsersLoadRequested>(_onLoadRequested);
    on<UsersSearchRequested>(_onSearchRequested);
    on<UsersDeleteRequested>(_onDeleteRequested);
    on<UsersRoleUpdateRequested>(_onRoleUpdateRequested);
  }

  // 处理加载用户列表请求
  Future<void> _onLoadRequested(
    UsersLoadRequested event,
    Emitter<UsersState> emit,
  ) async {
    emit(UsersLoading());
    try {
      final users = await _userRepository.getAllUsers();
      emit(UsersLoaded(users: users));
    } catch (e) {
      emit(UsersError(message: e.toString()));
    }
  }

  // 处理搜索用户请求
  Future<void> _onSearchRequested(
    UsersSearchRequested event,
    Emitter<UsersState> emit,
  ) async {
    emit(UsersLoading());
    try {
      final users = await _userRepository.getAllUsers(name: event.query);
      emit(UsersLoaded(users: users));
    } catch (e) {
      emit(UsersError(message: e.toString()));
    }
  }

  // 处理删除用户请求
  Future<void> _onDeleteRequested(
    UsersDeleteRequested event,
    Emitter<UsersState> emit,
  ) async {
    try {
      await _userRepository.deleteUser(event.userId);
      emit(UsersSuccess(message: '删除成功'));
      add(UsersLoadRequested());
    } catch (e) {
      emit(UsersError(message: e.toString()));
    }
  }

  // 处理更新用户角色请求
  Future<void> _onRoleUpdateRequested(
    UsersRoleUpdateRequested event,
    Emitter<UsersState> emit,
  ) async {
    try {
      await _userRepository.updateRole(event.userId, event.role);
      emit(UsersSuccess(message: '角色更新成功'));
      add(UsersLoadRequested());
    } catch (e) {
      emit(UsersError(message: e.toString()));
    }
  }
}
